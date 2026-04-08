package retry;

public class ExponentialBackoffRetryPolicy implements RetryPolicy {
    private final long initialDelayMillis;
    private final long maxDelayMillis;

    public ExponentialBackoffRetryPolicy(long initialDelayMillis, long maxDelayMillis) {
        if (initialDelayMillis <= 0 || maxDelayMillis <= 0) {
            throw new IllegalArgumentException("delays must be > 0");
        }
        if (initialDelayMillis > maxDelayMillis) {
            throw new IllegalArgumentException("initialDelayMillis cannot be greater than maxDelayMillis");
        }
        this.initialDelayMillis = initialDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
    }

    @Override
    public long calculateDelayMillis(int attemptNumber) {
        if (attemptNumber <= 0) {
            return initialDelayMillis;
        }

        long multiplier = 1L << Math.max(0, attemptNumber - 1);
        long delay = initialDelayMillis * multiplier;
        return Math.min(delay, maxDelayMillis);
    }
}