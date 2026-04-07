import domain.AttemptStatus;
import domain.BankResult;
import provider.ScriptedBankProvider;
import provider.VirtualBank;
import provider.VirtualBankPool;
import repository.InMemoryPayoutRepository;
import resilience.SemaphoreCapacityLimiter;
import resilience.SimpleCircuitBreaker;
import service.BankExecutionService;
import service.IdempotencyLockManager;
import service.PayoutService;
import strategy.PriorityBasedSelectionStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestHelper {

    public static BankResult success(String msg) {
        return new BankResult(AttemptStatus.SUCCESS, msg);
    }

    public static BankResult retryable(String msg) {
        return new BankResult(AttemptStatus.RETRYABLE_FAILURE, msg);
    }

    public static BankResult nonRetryable(String msg) {
        return new BankResult(AttemptStatus.NON_RETRYABLE_FAILURE, msg);
    }

    public static BankResult unknown(String msg) {
        return new BankResult(AttemptStatus.UNKNOWN, msg);
    }

    public static Queue<BankResult> queue(BankResult... results) {
        Queue<BankResult> q = new ConcurrentLinkedQueue<>();
        Collections.addAll(q, results);
        return q;
    }

    public static VirtualBank bank(ScriptedBankProvider provider, int capacity, int failureThreshold, long openMillis) {
        return new VirtualBank(
                provider,
                new SimpleCircuitBreaker(failureThreshold, openMillis),
                new SemaphoreCapacityLimiter(capacity)
        );
    }

    public static PayoutService service(List<VirtualBank> banks) {
        return new PayoutService(
                new InMemoryPayoutRepository(),
                new VirtualBankPool(banks),
                new PriorityBasedSelectionStrategy(
                        banks.stream().map(VirtualBank::name).toList()
                ),
                new IdempotencyLockManager(),
                new BankExecutionService()
        );
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Assertion failed: " + message);
        }
    }

    public static void printPass(String testName) {
        System.out.println("[PASS] " + testName);
    }
}
