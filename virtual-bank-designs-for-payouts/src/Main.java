import domain.Payout;
import domain.PayoutRequest;
import domain.PayoutStatus;
import provider.ScriptedBankProvider;
import provider.VirtualBank;
import service.PayoutService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) throws Exception {
        runBasicSuccessTest();
        runFallbackTest();
        runNonRetryableFailureTest();
        runUnknownFailureTest();
        runCircuitBreakerTest();
        runIdempotencyConcurrencyTest();
        runCapacityRoutingTest();
    }

    private static void runBasicSuccessTest() {
        ScriptedBankProvider bankAProvider =
                new ScriptedBankProvider("BankA", TestHelper.queue(TestHelper.success("success")), 0);

        VirtualBank bankA = TestHelper.bank(bankAProvider, 2, 3, 1000);
        PayoutService service = TestHelper.service(List.of(bankA));

        PayoutRequest request = new PayoutRequest("M1", "B1", 1000, "INR", "idem-1");
        Payout payout = service.process(request);

        TestHelper.assertTrue(payout.getStatus() == PayoutStatus.SUCCESS, "Payout should succeed");
        TestHelper.assertTrue("BankA".equals(payout.getSelectedBank()), "Selected bank should be BankA");
        TestHelper.assertTrue(bankAProvider.getInvocationCount() == 1, "BankA should be called once");
        TestHelper.assertTrue(payout.getAttempts().size() == 1, "One attempt expected");

        TestHelper.printPass("runBasicSuccessTest");
    }

    private static void runFallbackTest() {
        ScriptedBankProvider bankAProvider =
                new ScriptedBankProvider("BankA", TestHelper.queue(TestHelper.retryable("timeout")), 0);
        ScriptedBankProvider bankBProvider =
                new ScriptedBankProvider("BankB", TestHelper.queue(TestHelper.success("success")), 0);

        VirtualBank bankA = TestHelper.bank( bankAProvider, 2, 3, 1000);
        VirtualBank bankB = TestHelper.bank(bankBProvider, 2, 3, 1000);

        PayoutService service = TestHelper.service(List.of(bankA, bankB));

        Payout payout = service.process(new PayoutRequest("M1", "B1", 1000, "INR", "idem-2"));

        TestHelper.assertTrue(payout.getStatus() == PayoutStatus.SUCCESS, "Payout should succeed");
        TestHelper.assertTrue("BankB".equals(payout.getSelectedBank()), "Fallback should select BankB");
        TestHelper.assertTrue(bankAProvider.getInvocationCount() == 1, "BankA should be called once");
        TestHelper.assertTrue(bankBProvider.getInvocationCount() == 1, "BankB should be called once");
        TestHelper.assertTrue(payout.getAttempts().size() == 2, "Two attempts expected");

        TestHelper.printPass("runFallbackTest");
    }

    private static void runNonRetryableFailureTest() {
        ScriptedBankProvider bankAProvider =
                new ScriptedBankProvider("BankA", TestHelper.queue(TestHelper.nonRetryable("invalid beneficiary")), 0);
        ScriptedBankProvider bankBProvider =
                new ScriptedBankProvider("BankB", TestHelper.queue(TestHelper.success("should not be used")), 0);

        VirtualBank bankA = TestHelper.bank( bankAProvider, 2, 3, 1000);
        VirtualBank bankB = TestHelper.bank(bankBProvider, 2, 3, 1000);

        PayoutService service = TestHelper.service(List.of(bankA, bankB));

        Payout payout = service.process(new PayoutRequest("M1", "BAD_BEN", 1000, "INR", "idem-3"));

        TestHelper.assertTrue(payout.getStatus() == PayoutStatus.FAILED, "Payout should fail");
        TestHelper.assertTrue(bankAProvider.getInvocationCount() == 1, "BankA should be called once");
        TestHelper.assertTrue(bankBProvider.getInvocationCount() == 0, "BankB should not be called");
        TestHelper.assertTrue(payout.getAttempts().size() == 1, "Only one attempt expected");

        TestHelper.printPass("runNonRetryableFailureTest");
    }

    private static void runUnknownFailureTest() {
        ScriptedBankProvider bankAProvider =
                new ScriptedBankProvider("BankA", TestHelper.queue(TestHelper.unknown("timeout after submit")), 0);
        ScriptedBankProvider bankBProvider =
                new ScriptedBankProvider("BankB", TestHelper.queue(TestHelper.success("should not be used")), 0);

        VirtualBank bankA = TestHelper.bank( bankAProvider, 2, 3, 1000);
        VirtualBank bankB = TestHelper.bank( bankBProvider, 2, 3, 1000);

        PayoutService service = TestHelper.service(List.of(bankA, bankB));

        Payout payout = service.process(new PayoutRequest("M1", "B1", 1000, "INR", "idem-4"));

        TestHelper.assertTrue(payout.getStatus() == PayoutStatus.UNKNOWN, "Payout should be UNKNOWN");
        TestHelper.assertTrue("BankA".equals(payout.getSelectedBank()), "BankA should be recorded");
        TestHelper.assertTrue(bankAProvider.getInvocationCount() == 1, "BankA should be called once");
        TestHelper.assertTrue(bankBProvider.getInvocationCount() == 0, "BankB should not be called");

        TestHelper.printPass("runUnknownFailureTest");
    }

    private static void runCircuitBreakerTest() {
        ScriptedBankProvider bankAProvider =
                new ScriptedBankProvider(
                        "BankA",
                        TestHelper.queue(
                                TestHelper.retryable("fail1"),
                                TestHelper.retryable("fail2"),
                                TestHelper.retryable("fail3"),
                                TestHelper.success("should not be reached yet")
                        ),
                        0
                );

        ScriptedBankProvider bankBProvider =
                new ScriptedBankProvider(
                        "BankB",
                        TestHelper.queue(
                                TestHelper.success("ok1"),
                                TestHelper.success("ok2"),
                                TestHelper.success("ok3"),
                                TestHelper.success("ok4")
                        ),
                        0
                );

        VirtualBank bankA = TestHelper.bank( bankAProvider, 2, 2, 5000);
        VirtualBank bankB = TestHelper.bank( bankBProvider, 2, 3, 5000);

        PayoutService service = TestHelper.service(List.of(bankA, bankB));

        Payout payout1 = service.process(new PayoutRequest("M1", "B1", 100, "INR", "cb-1"));
        Payout payout2 = service.process(new PayoutRequest("M1", "B1", 100, "INR", "cb-2"));
        Payout payout3 = service.process(new PayoutRequest("M1", "B1", 100, "INR", "cb-3"));

        TestHelper.assertTrue(bankAProvider.getInvocationCount() == 2, "BankA should stop being called after breaker opens");
        TestHelper.assertTrue(bankBProvider.getInvocationCount() == 3, "BankB should handle all fallbacks / later requests");
        TestHelper.assertTrue(payout3.getStatus() == PayoutStatus.SUCCESS, "Third payout should still succeed via BankB");

        TestHelper.printPass("runCircuitBreakerTest");
    }


    private static void runIdempotencyConcurrencyTest() throws Exception {
        ScriptedBankProvider bankAProvider =
                new ScriptedBankProvider("BankA", TestHelper.queue(TestHelper.success("ok")), 200);

        VirtualBank bankA = TestHelper.bank( bankAProvider, 5, 3, 1000);
        PayoutService service = TestHelper.service(List.of(bankA));

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        List<Payout> results = new CopyOnWriteArrayList<>();

        PayoutRequest request = new PayoutRequest("M1", "B1", 500, "INR", "same-idem-key");

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    Payout payout = service.process(request);
                    results.add(payout);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        Set<String> payoutIds = results.stream().map(Payout::getPayoutId).collect(Collectors.toSet());

        TestHelper.assertTrue(results.size() == threadCount, "All threads should return a payout");
        TestHelper.assertTrue(payoutIds.size() == 1, "All threads should see same payout");
        TestHelper.assertTrue(bankAProvider.getInvocationCount() == 1, "Only one actual bank call should happen");
        TestHelper.assertTrue(results.get(0).getStatus() == PayoutStatus.SUCCESS, "Payout should succeed");

        TestHelper.printPass("runIdempotencyConcurrencyTest");
    }

    private static void runCapacityRoutingTest() throws Exception {
        ScriptedBankProvider bankAProvider =
                new ScriptedBankProvider("BankA", TestHelper.queue(
                        TestHelper.success("slow-success"),
                        TestHelper.success("unused")
                ), 500);

        ScriptedBankProvider bankBProvider =
                new ScriptedBankProvider("BankB", TestHelper.queue(
                        TestHelper.success("fast-success"),
                        TestHelper.success("fast-success-2")
                ), 0);

        VirtualBank bankA = TestHelper.bank( bankAProvider, 1, 3, 1000);
        VirtualBank bankB = TestHelper.bank( bankBProvider, 2, 3, 1000);

        PayoutService service = TestHelper.service(List.of(bankA, bankB));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);

        List<Payout> results = new CopyOnWriteArrayList<>();

        executor.submit(() -> {
            ready.countDown();
            try {
                start.await();
                results.add(service.process(new PayoutRequest("M1", "B1", 100, "INR", "cap-1")));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }
        });

        executor.submit(() -> {
            ready.countDown();
            try {
                start.await();
                results.add(service.process(new PayoutRequest("M2", "B2", 200, "INR", "cap-2")));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }
        });

        ready.await();
        start.countDown();
        done.await();
        executor.shutdown();

        long bankASuccessCount = results.stream().filter(p -> "BankA".equals(p.getSelectedBank())).count();
        long bankBSuccessCount = results.stream().filter(p -> "BankB".equals(p.getSelectedBank())).count();

        TestHelper.assertTrue(bankASuccessCount == 1, "Exactly one request should use BankA");
        TestHelper.assertTrue(bankBSuccessCount == 1, "One request should fallback to BankB due to capacity");
        TestHelper.assertTrue(bankAProvider.getInvocationCount() == 1, "BankA should only process one inflight request");

        TestHelper.printPass("runCapacityRoutingTest");
    }
}