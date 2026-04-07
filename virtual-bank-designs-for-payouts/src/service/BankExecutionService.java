package service;

import domain.AttemptStatus;
import domain.BankExecutionOutcome;
import domain.BankResult;
import domain.PayoutRequest;
import provider.VirtualBank;

public class BankExecutionService {

    public BankExecutionOutcome execute(VirtualBank bank, PayoutRequest request) {
        if (!bank.circuitBreaker().allowRequest()) {
            return BankExecutionOutcome.skipped("Circuit open");
        }

        boolean acquired = bank.capacityLimiter().tryAcquire();

        if (!acquired) {
            return BankExecutionOutcome.skipped("No capacity");
        }

        try {
            BankResult result = bank.provider().transfer(request);

            if (result.getStatus() == AttemptStatus.SUCCESS) {
                bank.circuitBreaker().recordSuccess();
            } else if (result.getStatus() == AttemptStatus.RETRYABLE_FAILURE
                    || result.getStatus() == AttemptStatus.UNKNOWN) {
                bank.circuitBreaker().recordFailure();
            } else if (result.getStatus() == AttemptStatus.NON_RETRYABLE_FAILURE) {
                // Provider is healthy; business error should not trip breaker
                bank.circuitBreaker().recordSuccess();
            }

            return BankExecutionOutcome.attempted(result);

        } catch (Exception e) {
            bank.circuitBreaker().recordFailure();
            return BankExecutionOutcome.attempted(
                    new BankResult(AttemptStatus.RETRYABLE_FAILURE, "Exception: " + e.getMessage())
            );
        } finally {
            if (acquired) {
                bank.capacityLimiter().release();
            }
        }
    }
}
