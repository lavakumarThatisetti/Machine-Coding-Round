package resilience;

import java.util.concurrent.Semaphore;

public class SemaphoreCapacityLimiter implements CapacityLimiter {
    private final Semaphore semaphore;

    public SemaphoreCapacityLimiter(int capacity) {
        this.semaphore = new Semaphore(capacity);
    }

    @Override
    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    @Override
    public void release() {
        semaphore.release();
    }

    @Override
    public int availableSlots() {
        return semaphore.availablePermits();
    }
}
