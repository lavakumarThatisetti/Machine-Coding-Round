package com.lavakumar.taskschedule;

import com.interview.taskschedule.retrypolicy.ExponentialBackoffPolicy;
import com.interview.taskschedule.task.TaskDefinition;
import com.interview.taskschedule.trigger.FixedRateTrigger;
import com.interview.taskschedule.trigger.OneTimeTrigger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() throws InterruptedException {
        DependencyManager dependencyManager = new DependencyManager();
        SchedulerEngine scheduler = new SchedulerEngine(4, dependencyManager);
        scheduler.start();

        // 1. A Happy Recurring Task
        TaskDefinition recurringDef = new TaskDefinition(
                "LogCleaner",
                () -> System.out.println("Running Log Cleaner..."),
                new FixedRateTrigger(2000), // Runs every 2 seconds
                new ExponentialBackoffPolicy(1000),
                3,
                new ArrayList<>(List.of("ClearGarbage"))
        );

        // 2. A Failing Task to demonstrate Retries
        TaskDefinition failingDef = new TaskDefinition(
                "FlakyAPI",
                () -> {
                    System.out.println("Attempting to call Flaky API...");
                    throw new RuntimeException("Network Timeout!");
                },
                new OneTimeTrigger(),
                new ExponentialBackoffPolicy(1000), // 1s, 2s, 4s backoff
                3,
                new ArrayList<>()
        );

        // 2. A Failing Task to demonstrate Retries
        TaskDefinition dependTask = new TaskDefinition(
                "ClearGarbage",
                () -> {
                    System.out.println("Cleared Garbage...");
                },
                new OneTimeTrigger(),
                new ExponentialBackoffPolicy(1000), // 1s, 2s, 4s backoff
                3,
                new ArrayList<>()
        );

        scheduler.submit(recurringDef);
        scheduler.submit(failingDef);
        scheduler.submit(dependTask);

        // Let it run for 10 seconds so we can observe the logs
        Thread.sleep(10000);
        scheduler.shutdown();

    }
}
