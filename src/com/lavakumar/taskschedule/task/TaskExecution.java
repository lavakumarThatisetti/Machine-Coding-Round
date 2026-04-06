package com.lavakumar.taskschedule.task;

import java.util.UUID;

// Runtime State (Implements Comparable for the Min-Heap)
public class TaskExecution implements Comparable<TaskExecution> {
    public final String executionId;
    public final TaskDefinition definition;
    public int attemptCount;
    public long nextExecutionTime;
    public TaskState state;

    public TaskExecution(TaskDefinition definition, long nextExecutionTime) {
        this.executionId = UUID.randomUUID().toString();
        this.definition = definition;
        this.attemptCount = 0;
        this.nextExecutionTime = nextExecutionTime;
        this.state = TaskState.SCHEDULED;
    }

    @Override
    public int compareTo(TaskExecution other) {
        return Long.compare(this.nextExecutionTime, other.nextExecutionTime);
    }
}
