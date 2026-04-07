package provider;

import resilience.CapacityLimiter;
import resilience.CircuitBreaker;

public class VirtualBank {
    private final BankProvider bankProvider;
    private final CircuitBreaker circuitBreaker;
    private final CapacityLimiter capacityLimiter;

    public VirtualBank(BankProvider bankProvider, CircuitBreaker circuitBreaker, CapacityLimiter capacityLimiter) {
        this.bankProvider = bankProvider;
        this.circuitBreaker = circuitBreaker;
        this.capacityLimiter = capacityLimiter;
    }

    public String name() {
        return bankProvider.getName();
    }

    public BankProvider provider() {
        return bankProvider;
    }

    public CircuitBreaker circuitBreaker() {
        return circuitBreaker;
    }

    public CapacityLimiter capacityLimiter() {
        return capacityLimiter;
    }
}
