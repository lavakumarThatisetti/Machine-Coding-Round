package model;

import retry.RetryPolicy;
import trigger.Trigger;

import java.util.List;

public class TaskDefinition {
    private final String taskId;
    private final String taskName;
    private final Task task;
    private final List<String> dependencies;
    private final Trigger trigger;
    private final RetryPolicy retryPolicy;
    private final int maxRetries;
    private final long timeoutMillis;

    public TaskDefinition(String taskId,
                          String taskName,
                          Task task,
                          List<String> dependencies,
                          Trigger trigger,
                          RetryPolicy retryPolicy,
                          int maxRetries,
                          long timeoutMillis) {

        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("taskId cannot be null/blank");
        }
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("taskName cannot be null/blank");
        }
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        if (trigger == null) {
            throw new IllegalArgumentException("trigger cannot be null");
        }
        if (retryPolicy == null) {
            throw new IllegalArgumentException("retryPolicy cannot be null");
        }
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries cannot be negative");
        }
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("timeoutMillis must be > 0");
        }

        this.taskId = taskId;
        this.taskName = taskName;
        this.task = task;
        this.dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
        this.trigger = trigger;
        this.retryPolicy = retryPolicy;
        this.maxRetries = maxRetries;
        this.timeoutMillis = timeoutMillis;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public Task getTask() {
        return task;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    @Override
    public String toString() {
        return "TaskDefinition{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", dependencies=" + dependencies +
                ", maxRetries=" + maxRetries +
                ", timeoutMillis=" + timeoutMillis +
                '}';
    }
}