package service;

import model.ScheduledTask;
import model.TaskDefinition;
import model.TaskRuntime;
import model.TaskStatus;
import repository.InMemoryTaskRepository;

import java.util.List;
import java.util.concurrent.*;

public class TaskExecutionService {
    private final InMemoryTaskRepository taskRepository;
    private final DependencyService dependencyService;
    private final DelayQueue<ScheduledTask> delayQueue;
    private final ExecutorService taskRunnerPool;

    public TaskExecutionService(InMemoryTaskRepository taskRepository,
                                DependencyService dependencyService,
                                DelayQueue<ScheduledTask> delayQueue,
                                ExecutorService taskRunnerPool) {
        this.taskRepository = taskRepository;
        this.dependencyService = dependencyService;
        this.delayQueue = delayQueue;
        this.taskRunnerPool = taskRunnerPool;
    }

    public void executeTask(String taskId) {
        TaskDefinition definition = taskRepository.getDefinition(taskId);
        TaskRuntime runtime = taskRepository.getRuntime(taskId);

        if (definition == null || runtime == null) {
            return;
        }

        boolean movedToRunning = runtime.compareAndSetStatus(TaskStatus.SCHEDULED, TaskStatus.RUNNING);
        if (!movedToRunning) {
            return;
        }

        runtime.setLastStartTime(System.currentTimeMillis());

        Future<?> future = taskRunnerPool.submit(() -> {
            definition.getTask().execute();
            return null;
        });

        try {
            future.get(definition.getTimeoutMillis(), TimeUnit.MILLISECONDS);
            onTaskSuccess(definition, runtime);
        } catch (TimeoutException e) {
            future.cancel(true);
            onTaskFailure(definition, runtime, "Task timed out");
        } catch (ExecutionException e) {
            future.cancel(true);
            Throwable cause = e.getCause() == null ? e : e.getCause();
            onTaskFailure(definition, runtime, "Execution failed: " + cause.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            onTaskFailure(definition, runtime, "Execution interrupted");
        }
    }

    private void onTaskSuccess(TaskDefinition definition, TaskRuntime runtime) {
        runtime.forceStatus(TaskStatus.SUCCEEDED);
        runtime.setLastEndTime(System.currentTimeMillis());
        runtime.setFailureReason(null);

        System.out.println("Task succeeded: " + definition.getTaskId());

        List<String> newlyReadyTaskIds = dependencyService.onTaskSucceeded(definition.getTaskId());
        for (String childTaskId : newlyReadyTaskIds) {
            TaskRuntime childRuntime = taskRepository.getRuntime(childTaskId);
            TaskDefinition childDefinition = taskRepository.getDefinition(childTaskId);

            if (childRuntime == null || childDefinition == null) {
                continue;
            }

            boolean moved = childRuntime.compareAndSetStatus(TaskStatus.WAITING_FOR_DEPENDENCIES, TaskStatus.SCHEDULED);
            if (moved) {
                long nextTime = childDefinition.getTrigger().nextExecutionTime(null);
                childRuntime.setNextExecutionTime(nextTime);
                delayQueue.offer(new ScheduledTask(childTaskId, nextTime));
                System.out.println("Child task unlocked and scheduled: " + childTaskId);
            }
        }
    }

    private void onTaskFailure(TaskDefinition definition, TaskRuntime runtime, String reason) {
        runtime.setLastEndTime(System.currentTimeMillis());
        runtime.setFailureReason(reason);

        int currentAttempt = runtime.incrementAttemptCount();

        if (currentAttempt <= definition.getMaxRetries()) {
            long delay = definition.getRetryPolicy().calculateDelayMillis(currentAttempt);
            long nextExecutionTime = System.currentTimeMillis() + delay;

            runtime.setNextExecutionTime(nextExecutionTime);
            runtime.forceStatus(TaskStatus.SCHEDULED);

            delayQueue.offer(new ScheduledTask(definition.getTaskId(), nextExecutionTime));

            System.out.println("Task failed, retry scheduled. taskId=" + definition.getTaskId()
                    + ", attempt=" + currentAttempt
                    + ", reason=" + reason);
            return;
        }

        runtime.forceStatus(TaskStatus.FAILED);
        System.out.println("Task failed permanently: " + definition.getTaskId() + ", reason=" + reason);

        List<String> downstreamTasks = dependencyService.onTaskFailed(definition.getTaskId());
        for (String childTaskId : downstreamTasks) {
            TaskRuntime childRuntime = taskRepository.getRuntime(childTaskId);
            if (childRuntime == null) {
                continue;
            }

            TaskStatus currentStatus = childRuntime.getStatus();
            if (currentStatus == TaskStatus.WAITING_FOR_DEPENDENCIES || currentStatus == TaskStatus.SCHEDULED) {
                childRuntime.forceStatus(TaskStatus.SKIPPED);
                childRuntime.setFailureReason("Skipped because dependency failed: " + definition.getTaskId());
                childRuntime.setLastEndTime(System.currentTimeMillis());

                System.out.println("Task skipped due to dependency failure: " + childTaskId);
            }
        }
    }
}