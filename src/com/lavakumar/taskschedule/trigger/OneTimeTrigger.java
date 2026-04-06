package com.lavakumar.taskschedule.trigger;

public class OneTimeTrigger implements Trigger {
    @Override
    public Long getNextFireTime(Long lastExecutionTime) {
        return null; // Never runs again
    }
}
