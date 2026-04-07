package provider;

import domain.AttemptStatus;
import domain.BankResult;
import domain.PayoutRequest;

import java.util.concurrent.atomic.AtomicInteger;

public class FakeBankProvider implements BankProvider {
    private final String name;
    private final AttemptStatus fixedStatus;
    private final String message;
    private final AtomicInteger invocationCount = new AtomicInteger(0);

    public FakeBankProvider(String name, AttemptStatus fixedStatus, String message) {
        this.name = name;
        this.fixedStatus = fixedStatus;
        this.message = message;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BankResult transfer(PayoutRequest request) {
        invocationCount.incrementAndGet();
        return new BankResult(fixedStatus, message);
    }

    public int getInvocationCount() {
        return invocationCount.get();
    }
}
