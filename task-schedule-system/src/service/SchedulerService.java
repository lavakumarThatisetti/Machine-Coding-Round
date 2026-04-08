package service;

import model.ScheduledTask;
import model.TaskDefinition;
import model.TaskRuntime;
import model.TaskStatus;
import repository.InMemoryTaskRepository;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SchedulerService {
    private final InMemoryTaskRepository taskRepository;
    private final DependencyService dependencyService;

    private final DelayQueue<ScheduledTask> delayQueue;
    private final ExecutorService dispatcherExecutor;
    private final ExecutorService taskRunnerPool;

    private final TaskExecutionService taskExecutionService;

    private volatile boolean running = false;

    public SchedulerService(int workerThreads) {
        this.taskRepository = new InMemoryTaskRepository();
        this.dependencyService = new DependencyService(taskRepository);
        this.delayQueue = new DelayQueue<>();
        this.dispatcherExecutor = Executors.newSingleThreadExecutor();
        this.taskRunnerPool = Executors.newFixedThreadPool(workerThreads);
        this.taskExecutionService = new TaskExecutionService(
                taskRepository,
                dependencyService,
                delayQueue,
                taskRunnerPool
        );
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;
        dispatcherExecutor.submit(() -> {
            while (running) {
                try {
                    ScheduledTask scheduledTask = delayQueue.take();
                    taskExecutionService.executeTask(scheduledTask.taskId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.out.println("Dispatcher error: " + e.getMessage());
                }
            }
        });
    }

    public void submit(TaskDefinition taskDefinition) {
        taskRepository.saveDefinition(taskDefinition);

        DependencyRegistrationResult result = dependencyService.register(taskDefinition);

        if (result.isBlockedByFailedDependency()) {
            TaskRuntime runtime = new TaskRuntime(
                    taskDefinition.getTaskId(),
                    TaskStatus.SKIPPED,
                    -1
            );
            runtime.setFailureReason("Blocked because one dependency already failed");
            runtime.setLastEndTime(System.currentTimeMillis());
            taskRepository.saveRuntime(runtime);

            System.out.println("Task skipped at registration due to failed dependency: " + taskDefinition.getTaskId());
            return;
        }

        if (result.isReadyToSchedule()) {
            long nextExecutionTime = taskDefinition.getTrigger().nextExecutionTime(null);
            TaskRuntime runtime = new TaskRuntime(
                    taskDefinition.getTaskId(),
                    TaskStatus.SCHEDULED,
                    nextExecutionTime
            );
            taskRepository.saveRuntime(runtime);

            delayQueue.offer(new ScheduledTask(taskDefinition.getTaskId(), nextExecutionTime));
            System.out.println("Task scheduled immediately: " + taskDefinition.getTaskId());
            return;
        }

        TaskRuntime runtime = new TaskRuntime(
                taskDefinition.getTaskId(),
                TaskStatus.WAITING_FOR_DEPENDENCIES,
                -1
        );
        taskRepository.saveRuntime(runtime);

        System.out.println("Task registered and waiting for dependencies: " + taskDefinition.getTaskId()
                + ", remainingDependencies=" + result.getRemainingDependencies());
    }

    public TaskRuntime getTaskRuntime(String taskId) {
        return taskRepository.getRuntime(taskId);
    }

    public void shutdown() {
        running = false;
        dispatcherExecutor.shutdownNow();
        taskRunnerPool.shutdownNow();
    }
}