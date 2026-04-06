package com.lavakumar.taskschedule;

import com.interview.taskschedule.task.TaskDefinition;
import com.interview.taskschedule.task.TaskExecution;
import com.interview.taskschedule.task.TaskState;

import java.util.List;
import java.util.concurrent.*;

public class SchedulerEngine {
    // The Min-Heap ordered by nextExecutionTime
    private final PriorityBlockingQueue<TaskExecution> taskQueue = new PriorityBlockingQueue<>();

    // The workers that actually run the tasks
    private final ExecutorService workerPool;

    // The lightweight thread that polls the queue
    private final ScheduledExecutorService tickExecutor;

    private final DependencyManager dependencyManager;

    public SchedulerEngine(int workerThreads, DependencyManager dependencyManager) {
        this.workerPool = Executors.newFixedThreadPool(workerThreads);
        this.dependencyManager = dependencyManager;
        this.tickExecutor = Executors.newSingleThreadScheduledExecutor();
    }


    public void start() {
        // The Tick Thread runs every 100ms to check the queue
        tickExecutor.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
        System.out.println("Scheduler started...");
    }

    public void submit(TaskDefinition definition) {
        // Ask the DAG if it can run right now
        if (dependencyManager.registerTask(definition)) {
            // It has 0 dependencies, schedule it normally
            long firstRunTime = definition.trigger.getNextFireTime(null) != null
                    ? definition.trigger.getNextFireTime(null)
                    : System.currentTimeMillis();

            taskQueue.offer(new TaskExecution(definition, firstRunTime));
        } else {
            System.out.println("Task " + definition.id + " is parked waiting for dependencies.");
        }
    }

    public void tick() {
        long now = System.currentTimeMillis();
        // Peek at the top of the heap. If it's time to run, pop it and send to worker.
        while (!taskQueue.isEmpty() && taskQueue.peek().nextExecutionTime <= now) {
            TaskExecution execution = taskQueue.poll();
            if (execution != null) {
                execution.state = TaskState.RUNNING;
                workerPool.submit(()-> executeTask(execution));
            }
        }
    }

    public void executeTask(TaskExecution execution) {
        TaskDefinition def = execution.definition;
        try {
            // 1. DO THE WORK
            def.task.execute();
            execution.state = TaskState.SUCCESS;
            System.out.println("SUCCESS: Task " + def.id + " completed.");

            // 2. DAG UNLOCK: Tell the manager this is done
            List<TaskDefinition> unlockedTasks = dependencyManager.markTaskComplete(def.id);
            for (TaskDefinition unlockedTask : unlockedTasks) {
                // Submit the newly unlocked tasks directly to the queue
                long runTime = unlockedTask.trigger.getNextFireTime(null) != null
                        ? unlockedTask.trigger.getNextFireTime(null)
                        : System.currentTimeMillis();
                taskQueue.offer(new TaskExecution(unlockedTask, runTime));
                System.out.println("DAG UNLOCKED: " + unlockedTask.id + " is now ready.");
            }

            // 2. HAPPY PATH: Check if it's recurring
            Long nextTime = def.trigger.getNextFireTime(System.currentTimeMillis());
            if (nextTime != null) {
                TaskExecution nextRun = new TaskExecution(def, nextTime);
                taskQueue.offer(nextRun);
                System.out.println("RECURRING: Rescheduled " + def.id + " for " + nextTime);
            }

        } catch (Exception e) {
            // 3. SAD PATH: Handle Retries
            System.out.println("FAILED: Task " + def.id + " failed. Reason: " + e.getMessage());
            if (execution.attemptCount < def.maxRetries) {
                execution.attemptCount++;
                long delay = def.retryPolicy.calculateBackoffDelay(execution.attemptCount);
                execution.nextExecutionTime = System.currentTimeMillis() + delay;
                execution.state = TaskState.SCHEDULED;

                System.out.println("RETRYING: Task " + def.id + " attempt " + execution.attemptCount + ". Next run in " + delay + "ms");
                taskQueue.offer(execution); // Push back into the heap
            } else {
                execution.state = TaskState.FAILED;
                System.out.println("DEAD LETTER: Task " + def.id + " exhausted all retries.");
            }
        }
    }
    public void shutdown() {
        tickExecutor.shutdown();
        workerPool.shutdown();
    }
}
