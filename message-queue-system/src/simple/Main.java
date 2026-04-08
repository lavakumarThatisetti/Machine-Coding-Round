package simple;

import simple.model.Message;
import simple.queue.InMemoryBoundedSimpleMessageQueue;
import simple.queue.SimpleMessageQueue;
import simple.service.ConsumerService;
import simple.service.ProducerService;
import simple.simulation.ConsumerTask;
import simple.simulation.ProducerTask;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        main.testSingleProducerSingleConsumer();
        main.testMultipleProducersMultipleConsumers();
        main.testConsumerBlocksWhenQueueEmpty();
        main.testProducerBlocksWhenQueueFull();
        main.testShutdownWakesWaitingConsumers();

        System.out.println("\nAll tests passed.");
    }

    private void testSingleProducerSingleConsumer() throws Exception {
        System.out.println("\n====================================");
        System.out.println("TEST 1: Single Producer Single Consumer");
        System.out.println("====================================");

        SimpleMessageQueue queue = new InMemoryBoundedSimpleMessageQueue(5);
        ProducerService producerService = new ProducerService(queue);
        ConsumerService consumerService = new ConsumerService(queue);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        Set<String> consumedIds = ConcurrentHashMap.newKeySet();

        Future<?> producerFuture = executor.submit(
                new ProducerTask("P1", producerService, 5, 20, startLatch, producedCount)
        );
        Future<?> consumerFuture = executor.submit(
                new ConsumerTask("C1", consumerService, 30, startLatch, consumedCount, consumedIds)
        );

        startLatch.countDown();

        producerFuture.get();
        waitUntil(() -> consumedCount.get() == 5, 3000);

        queue.shutdown();
        consumerFuture.get();

        assertEquals(5, producedCount.get(), "Produced count mismatch");
        assertEquals(5, consumedCount.get(), "Consumed count mismatch");
        assertEquals(5, consumedIds.size(), "Consumed unique id count mismatch");
        assertEquals(0, queue.size(), "Queue should be empty");

        shutdownExecutor(executor);
        System.out.println("TEST 1 PASSED");
    }

    private void testMultipleProducersMultipleConsumers() throws Exception {
        System.out.println("\n====================================");
        System.out.println("TEST 2: Multiple Producers Multiple Consumers");
        System.out.println("====================================");

        SimpleMessageQueue queue = new InMemoryBoundedSimpleMessageQueue(10);
        ProducerService producerService = new ProducerService(queue);
        ConsumerService consumerService = new ConsumerService(queue);

        ExecutorService executor = Executors.newFixedThreadPool(6);

        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        Set<String> consumedIds = ConcurrentHashMap.newKeySet();

        Future<?> p1 = executor.submit(new ProducerTask("P1", producerService, 10, 10, startLatch, producedCount));
        Future<?> p2 = executor.submit(new ProducerTask("P2", producerService, 10, 15, startLatch, producedCount));
        Future<?> p3 = executor.submit(new ProducerTask("P3", producerService, 10, 12, startLatch, producedCount));

        Future<?> c1 = executor.submit(new ConsumerTask("C1", consumerService, 20, startLatch, consumedCount, consumedIds));
        Future<?> c2 = executor.submit(new ConsumerTask("C2", consumerService, 25, startLatch, consumedCount, consumedIds));
        Future<?> c3 = executor.submit(new ConsumerTask("C3", consumerService, 30, startLatch, consumedCount, consumedIds));

        startLatch.countDown();

        p1.get();
        p2.get();
        p3.get();

        waitUntil(() -> consumedCount.get() == 30, 5000);

        queue.shutdown();

        c1.get();
        c2.get();
        c3.get();

        assertEquals(30, producedCount.get(), "Produced count mismatch");
        assertEquals(30, consumedCount.get(), "Consumed count mismatch");
        assertEquals(30, consumedIds.size(), "Duplicate message consumed");
        assertEquals(0, queue.size(), "Queue should be empty");

        shutdownExecutor(executor);
        System.out.println("TEST 2 PASSED");
    }

    private void testConsumerBlocksWhenQueueEmpty() throws Exception {
        System.out.println("\n====================================");
        System.out.println("TEST 3: Consumer Blocks When Queue Empty");
        System.out.println("====================================");

        SimpleMessageQueue queue = new InMemoryBoundedSimpleMessageQueue(3);
        ConsumerService consumerService = new ConsumerService(queue);
        ProducerService producerService = new ProducerService(queue);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger consumedCount = new AtomicInteger(0);
        Set<String> consumedIds = ConcurrentHashMap.newKeySet();

        Future<?> consumerFuture = executor.submit(
                new ConsumerTask("C1", consumerService, 0, startLatch, consumedCount, consumedIds)
        );

        startLatch.countDown();

        Thread.sleep(300);
        assertEquals(0, consumedCount.get(), "Consumer should still be blocked when queue is empty");

        producerService.publish("P1", "first-message");

        waitUntil(() -> consumedCount.get() == 1, 2000);

        queue.shutdown();
        consumerFuture.get();

        assertEquals(1, consumedCount.get(), "Consumer should consume after message arrives");
        assertEquals(1, consumedIds.size(), "Exactly one message should be consumed");

        shutdownExecutor(executor);
        System.out.println("TEST 3 PASSED");
    }

    private void testProducerBlocksWhenQueueFull() throws Exception {
        System.out.println("\n====================================");
        System.out.println("TEST 4: Producer Blocks When Queue Full");
        System.out.println("====================================");

        SimpleMessageQueue queue = new InMemoryBoundedSimpleMessageQueue(2);
        ProducerService producerService = new ProducerService(queue);
        ConsumerService consumerService = new ConsumerService(queue);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        producerService.publish("P1", "m1");
        producerService.publish("P1", "m2");

        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger producedCount = new AtomicInteger(0);

        Future<?> blockedProducer = executor.submit(
                new ProducerTask("P2", producerService, 1, 0, startLatch, producedCount)
        );

        startLatch.countDown();

        Thread.sleep(300);
        assertEquals(0, producedCount.get(), "Producer should be blocked because queue is full");
        assertEquals(2, queue.size(), "Queue should still be full");

        Message consumed = consumerService.consumeNext();
        if (consumed == null) {
            throw new AssertionError("Expected a message but got null");
        }

        waitUntil(() -> producedCount.get() == 1, 2000);

        blockedProducer.get();

        assertEquals(2, queue.size(), "Queue should again have 2 messages after producer resumes");

        queue.shutdown();
        shutdownExecutor(executor);
        System.out.println("TEST 4 PASSED");
    }

    private void testShutdownWakesWaitingConsumers() throws Exception {
        System.out.println("\n====================================");
        System.out.println("TEST 5: Shutdown Wakes Waiting Consumers");
        System.out.println("====================================");

        SimpleMessageQueue queue = new InMemoryBoundedSimpleMessageQueue(2);
        ConsumerService consumerService = new ConsumerService(queue);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger consumedCount = new AtomicInteger(0);
        Set<String> consumedIds = ConcurrentHashMap.newKeySet();

        Future<?> c1 = executor.submit(
                new ConsumerTask("C1", consumerService, 0, startLatch, consumedCount, consumedIds)
        );
        Future<?> c2 = executor.submit(
                new ConsumerTask("C2", consumerService, 0, startLatch, consumedCount, consumedIds)
        );

        startLatch.countDown();

        Thread.sleep(300);
        queue.shutdown();

        c1.get();
        c2.get();

        assertEquals(0, consumedCount.get(), "No message should have been consumed");
        assertEquals(0, queue.size(), "Queue should remain empty");

        shutdownExecutor(executor);
        System.out.println("TEST 5 PASSED");
    }

    private void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ". Expected=" + expected + ", Actual=" + actual);
        }
    }

    private void waitUntil(Check check, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (check.evaluate()) {
                return;
            }
            Thread.sleep(20);
        }
        throw new AssertionError("Condition not met within timeout");
    }

    private void shutdownExecutor(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        if (!terminated) {
            executor.shutdownNow();
            boolean forcedTermination = executor.awaitTermination(5, TimeUnit.SECONDS);
            if (!forcedTermination) {
                throw new AssertionError("Executor did not terminate");
            }
        }
    }

    @FunctionalInterface
    private interface Check {
        boolean evaluate();
    }
}