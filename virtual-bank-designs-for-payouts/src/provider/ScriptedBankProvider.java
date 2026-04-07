package provider;

import domain.AttemptStatus;
import domain.BankResult;
import domain.PayoutRequest;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ScriptedBankProvider implements BankProvider {
    private final String name;
    private final Queue<BankResult> scriptedResults;
    private final AtomicInteger invocationCount = new AtomicInteger(0);
    private final long delayMillis;

    public ScriptedBankProvider(String name, Queue<BankResult> scriptedResults, long delayMillis) {
        this.name = name;
        this.scriptedResults = scriptedResults;
        this.delayMillis = delayMillis;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BankResult transfer(PayoutRequest request) {
        invocationCount.incrementAndGet();

        if (delayMillis > 0) {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new BankResult(AttemptStatus.RETRYABLE_FAILURE, "Interrupted");
            }
        }

        BankResult result = scriptedResults.poll();
        if (result == null) {
            return new BankResult(AttemptStatus.SUCCESS, "Default success");
        }
        return result;
    }

    public int getInvocationCount() {
        return invocationCount.get();
    }
}
