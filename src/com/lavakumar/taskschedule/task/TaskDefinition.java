package com.lavakumar.taskschedule.task;

import com.interview.taskschedule.retrypolicy.RetryPolicy;
import com.interview.taskschedule.trigger.Trigger;

import java.util.List;

public class TaskDefinition {
    public final String id;
    public final Task task;
    public final Trigger trigger;
    public final RetryPolicy retryPolicy;
    public final int maxRetries;
    public final List<String> dependencies;


    public TaskDefinition(String id, Task task, Trigger trigger, RetryPolicy retryPolicy, int maxRetries, List<String> dependencies) {
        this.id = id;
        this.task = task;
        this.trigger = trigger;
        this.retryPolicy = retryPolicy;
        this.maxRetries = maxRetries;
        this.dependencies = dependencies;
    }
}
