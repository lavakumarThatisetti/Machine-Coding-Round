package com.lavakumar.taskschedule.retrypolicy;

public class ExponentialBackoffPolicy implements RetryPolicy{
    private final long initialDelay;

    public ExponentialBackoffPolicy(long initialDelay) { this.initialDelay = initialDelay; }

    @Override
    public long calculateBackoffDelay(int attemptCount) {
        return initialDelay * (long) Math.pow(2, attemptCount);
    }
}
