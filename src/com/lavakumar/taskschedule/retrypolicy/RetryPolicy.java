package com.lavakumar.taskschedule.retrypolicy;

// --- The Sad Path (Retry Strategy) ---
public interface RetryPolicy {
    long calculateBackoffDelay(int attemptCount);
}
