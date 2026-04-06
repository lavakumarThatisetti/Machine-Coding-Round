package com.lavakumar.taskschedule.trigger;

// --- The Happy Path (Trigger Strategy) ---
public interface Trigger {
    // Returns the next epoch milli to run, or null if it shouldn't run again
    Long getNextFireTime(Long lastExecutionTime);
}
