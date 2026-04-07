package resilience;

/*
    object-level state → synchronized is fine
        state is local to one object
        transitions must be atomic
        critical section is small
        readability matters
 */
public class SimpleCircuitBreaker implements CircuitBreaker {
    private final int failureThreshold;
    private final long openDurationMillis;

    private int consecutiveFailures = 0;
    private CircuitState state = CircuitState.CLOSED;
    private long openedAt = -1L;

    public SimpleCircuitBreaker(int failureThreshold, long openDurationMillis) {
        this.failureThreshold = failureThreshold;
        this.openDurationMillis = openDurationMillis;
    }

    @Override
    public synchronized boolean allowRequest() {
        if (state == CircuitState.CLOSED) {
            return true;
        }

        if (state == CircuitState.OPEN) {
            long now = System.currentTimeMillis();
            if (now - openedAt >= openDurationMillis) {
                state = CircuitState.HALF_OPEN;
                return true;
            }
            return false;
        }

        return true; // HALF_OPEN allow probe request
    }

    @Override
    public synchronized void recordSuccess() {
        consecutiveFailures = 0;
        state = CircuitState.CLOSED;
        openedAt = -1L;
    }

    @Override
    public synchronized void recordFailure() {
        consecutiveFailures++;
        if (state == CircuitState.HALF_OPEN || consecutiveFailures >= failureThreshold) {
            state = CircuitState.OPEN;
            openedAt = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized CircuitState currentState() {
        return state;
    }
}
