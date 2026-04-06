package com.lavakumar.taskschedule.trigger;

public class FixedRateTrigger implements Trigger {
    private final long intervalMillis;
    public FixedRateTrigger(long intervalMillis) { this.intervalMillis = intervalMillis; }

    @Override
    public Long getNextFireTime(Long lastExecutionTime) {
        return (lastExecutionTime == null ? System.currentTimeMillis() : lastExecutionTime) + intervalMillis;
    }
}
