import model.TaskDefinition;
import model.TaskRuntime;
import retry.ExponentialBackoffRetryPolicy;
import service.SchedulerService;
import trigger.OneTimeTrigger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.run();
    }

    public void run() throws Exception {
        testSimpleSuccess();
        testRetryThenSuccess();
        testRetryThenPermanentFailure();
        testDependencyChainSuccess();
        testDependencyFailureCascade();
        testTimeout();
    }

    private void testSimpleSuccess() throws Exception {
        System.out.println("\n================ testSimpleSuccess ================");

        SchedulerService schedulerService = new SchedulerService(4);
        schedulerService.start();

        TaskDefinition task = new TaskDefinition(
                "T1",
                "SimpleSuccessTask",
                () -> System.out.println("Executing T1"),
                List.of(),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 5000),
                3,
                3000
        );

        schedulerService.submit(task);

        Thread.sleep(2000);
        printStatus(schedulerService, "T1");
        schedulerService.shutdown();
    }

    private void testRetryThenSuccess() throws Exception {
        System.out.println("\n================ testRetryThenSuccess ================");

        SchedulerService schedulerService = new SchedulerService(4);
        schedulerService.start();

        AtomicInteger counter = new AtomicInteger(0);

        TaskDefinition task = new TaskDefinition(
                "T2",
                "RetryThenSuccessTask",
                () -> {
                    int value = counter.incrementAndGet();
                    System.out.println("Executing T2, attempt=" + value);
                    if (value < 3) {
                        throw new RuntimeException("Simulated failure");
                    }
                    System.out.println("T2 succeeded on attempt=" + value);
                },
                List.of(),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 5000),
                3,
                3000
        );

        schedulerService.submit(task);

        Thread.sleep(5000);
        printStatus(schedulerService, "T2");
        schedulerService.shutdown();
    }

    private void testRetryThenPermanentFailure() throws Exception {
        System.out.println("\n================ testRetryThenPermanentFailure ================");

        SchedulerService schedulerService = new SchedulerService(4);
        schedulerService.start();

        TaskDefinition task = new TaskDefinition(
                "T3",
                "PermanentFailureTask",
                () -> {
                    System.out.println("Executing T3 and failing");
                    throw new RuntimeException("Always failing");
                },
                List.of(),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 5000),
                2,
                3000
        );

        schedulerService.submit(task);

        Thread.sleep(5000);
        printStatus(schedulerService, "T3");
        schedulerService.shutdown();
    }

    private void testDependencyChainSuccess() throws Exception {
        System.out.println("\n================ testDependencyChainSuccess ================");

        SchedulerService schedulerService = new SchedulerService(4);
        schedulerService.start();

        TaskDefinition t1 = new TaskDefinition(
                "D1",
                "ParentTask",
                () -> System.out.println("Executing D1"),
                List.of(),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 5000),
                2,
                3000
        );

        TaskDefinition t2 = new TaskDefinition(
                "D2",
                "ChildTask",
                () -> System.out.println("Executing D2"),
                List.of("D1"),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 5000),
                2,
                3000
        );

        TaskDefinition t3 = new TaskDefinition(
                "D3",
                "GrandChildTask",
                () -> System.out.println("Executing D3"),
                List.of("D2"),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 5000),
                2,
                3000
        );

        schedulerService.submit(t1);
        schedulerService.submit(t2);
        schedulerService.submit(t3);

        Thread.sleep(5000);
        printStatus(schedulerService, "D1");
        printStatus(schedulerService, "D2");
        printStatus(schedulerService, "D3");
        schedulerService.shutdown();
    }

    private void testDependencyFailureCascade() throws Exception {
        System.out.println("\n================ testDependencyFailureCascade ================");

        SchedulerService schedulerService = new SchedulerService(4);
        schedulerService.start();

        TaskDefinition t1 = new TaskDefinition(
                "F1",
                "FailingParentTask",
                () -> {
                    System.out.println("Executing F1");
                    throw new RuntimeException("Parent failed");
                },
                List.of(),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 2000),
                1,
                3000
        );

        TaskDefinition t2 = new TaskDefinition(
                "F2",
                "ChildTask",
                () -> System.out.println("Executing F2"),
                List.of("F1"),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 2000),
                1,
                3000
        );

        TaskDefinition t3 = new TaskDefinition(
                "F3",
                "GrandChildTask",
                () -> System.out.println("Executing F3"),
                List.of("F2"),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 2000),
                1,
                3000
        );

        schedulerService.submit(t1);
        schedulerService.submit(t2);
        schedulerService.submit(t3);

        Thread.sleep(5000);
        printStatus(schedulerService, "F1");
        printStatus(schedulerService, "F2");
        printStatus(schedulerService, "F3");
        schedulerService.shutdown();
    }

    private void testTimeout() throws Exception {
        System.out.println("\n================ testTimeout ================");

        SchedulerService schedulerService = new SchedulerService(4);
        schedulerService.start();

        TaskDefinition task = new TaskDefinition(
                "TIME1",
                "TimeoutTask",
                () -> {
                    System.out.println("Executing TIME1 and sleeping");
                    Thread.sleep(5000);
                    System.out.println("TIME1 woke up");
                },
                List.of(),
                new OneTimeTrigger(),
                new ExponentialBackoffRetryPolicy(500, 2000),
                1,
                1000
        );

        schedulerService.submit(task);

        Thread.sleep(5000);
        printStatus(schedulerService, "TIME1");
        schedulerService.shutdown();
    }

    private void printStatus(SchedulerService schedulerService, String taskId) {
        TaskRuntime runtime = schedulerService.getTaskRuntime(taskId);
        System.out.println("Final Runtime for " + taskId + " => " + runtime);
    }
}