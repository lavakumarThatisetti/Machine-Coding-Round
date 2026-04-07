package resilience;

public interface CircuitBreaker {
    boolean allowRequest();
    void recordSuccess();
    void recordFailure();
    CircuitState currentState();
}
