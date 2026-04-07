package resilience;

public interface CapacityLimiter {
    boolean tryAcquire();
    void release();
    int availableSlots();
}
