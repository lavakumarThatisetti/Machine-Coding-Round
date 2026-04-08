package retry;

public interface RetryPolicy {
    long calculateDelayMillis(int attemptNumber);
}