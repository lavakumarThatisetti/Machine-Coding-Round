package sqs;

import sqs.model.Delivery;
import sqs.model.Message;
import sqs.queue.AckMessageQueue;
import sqs.queue.SqsStyleMessageQueue;
import sqs.service.AckConsumerService;
import sqs.service.AckProducerService;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        main.testRetryOnNack();
        main.testRetryOnVisibilityTimeout();

        System.out.println("\nAll SQS-style queue tests passed.");
    }

    private void testRetryOnNack() throws Exception {
        System.out.println("\n====================================");
        System.out.println("TEST 1: Retry On Explicit Nack");
        System.out.println("====================================");

        AckMessageQueue queue = new SqsStyleMessageQueue(5_000);
        AckProducerService producerService = new AckProducerService(queue);
        AckConsumerService consumerService = new AckConsumerService(queue);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger processingAttempts = new AtomicInteger(0);
        AtomicInteger ackCount = new AtomicInteger(0);
        AtomicInteger nackCount = new AtomicInteger(0);

        List<String> deliveredMessageIds = new CopyOnWriteArrayList<>();
        List<Integer> receiveCountsSeen = new CopyOnWriteArrayList<>();

        Message published = producerService.publish("P1", "order-created");

        Future<?> consumerFuture = executor.submit(() -> {
            try {
                startLatch.await();

                while (true) {
                    Delivery delivery = consumerService.receive();
                    if (delivery == null) {
                        break;
                    }

                    deliveredMessageIds.add(delivery.getMessage().getMessageId());
                    receiveCountsSeen.add(delivery.getReceiveCount());

                    int currentAttempt = processingAttempts.incrementAndGet();

                    System.out.printf(
                            "[Consumer=C1] Received messageId=%s receiptHandle=%s receiveCount=%d attempt=%d%n",
                            delivery.getMessage().getMessageId(),
                            delivery.getReceiptHandle(),
                            delivery.getReceiveCount(),
                            currentAttempt
                    );

                    if (currentAttempt == 1) {
                        System.out.println("[Consumer=C1] Simulating failure. Sending NACK...");
                        consumerService.nack(delivery.getReceiptHandle());
                        nackCount.incrementAndGet();
                    } else {
                        System.out.println("[Consumer=C1] Simulating success. Sending ACK...");
                        consumerService.ack(delivery.getReceiptHandle());
                        ackCount.incrementAndGet();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Consumer interrupted", e);
            }
        });

        startLatch.countDown();

        consumerFuture.get(5, TimeUnit.SECONDS);

        waitUntil(() -> queue.visibleSize() == 0 && queue.inFlightSize() == 0, 2000);

        assertEquals(2, processingAttempts.get(), "Processing attempts mismatch");
        assertEquals(1, nackCount.get(), "Nack count mismatch");
        assertEquals(1, ackCount.get(), "Ack count mismatch");
        assertEquals(2, deliveredMessageIds.size(), "Message should be delivered twice");
        assertEquals(2, receiveCountsSeen.size(), "Receive counts should have 2 entries");
        assertEquals(published.getMessageId(), deliveredMessageIds.get(0), "First delivery messageId mismatch");
        assertEquals(published.getMessageId(), deliveredMessageIds.get(1), "Second delivery messageId mismatch");
        assertEquals(1, receiveCountsSeen.get(0), "First delivery receiveCount should be 1");
        assertEquals(2, receiveCountsSeen.get(1), "Second delivery receiveCount should be 2");
        assertEquals(0, queue.visibleSize(), "Visible queue should be empty");
        assertEquals(0, queue.inFlightSize(), "In-flight queue should be empty");

        queue.shutdown();
        shutdownExecutor(executor);

        System.out.println("TEST 1 PASSED");
    }

    private void testRetryOnVisibilityTimeout() throws Exception {
        System.out.println("\n====================================");
        System.out.println("TEST 2: Retry On Visibility Timeout");
        System.out.println("====================================");

        AckMessageQueue queue = new SqsStyleMessageQueue(500);
        AckProducerService producerService = new AckProducerService(queue);
        AckConsumerService consumerService = new AckConsumerService(queue);

        Message published = producerService.publish("P1", "payment-processed");

        Delivery firstDelivery = consumerService.receive();
        if (firstDelivery == null) {
            throw new AssertionError("Expected first delivery but got null");
        }

        assertEquals(published.getMessageId(), firstDelivery.getMessage().getMessageId(), "First delivery messageId mismatch");
        assertEquals(1, firstDelivery.getReceiveCount(), "First receiveCount should be 1");
        assertEquals(0, queue.visibleSize(), "Visible queue should be empty while message is in flight");
        assertEquals(1, queue.inFlightSize(), "In-flight queue should have 1 message");

        System.out.printf(
                "[Consumer=C1] Received messageId=%s receiptHandle=%s receiveCount=%d and intentionally not ACKing%n",
                firstDelivery.getMessage().getMessageId(),
                firstDelivery.getReceiptHandle(),
                firstDelivery.getReceiveCount()
        );

        Thread.sleep(700);

        Delivery secondDelivery = consumerService.receive();
        if (secondDelivery == null) {
            throw new AssertionError("Expected retry delivery after visibility timeout but got null");
        }

        assertEquals(published.getMessageId(), secondDelivery.getMessage().getMessageId(), "Retried delivery messageId mismatch");
        assertEquals(2, secondDelivery.getReceiveCount(), "Retried receiveCount should be 2");

        consumerService.ack(secondDelivery.getReceiptHandle());

        waitUntil(() -> queue.visibleSize() == 0 && queue.inFlightSize() == 0, 2000);

        assertEquals(0, queue.visibleSize(), "Visible queue should be empty after ack");
        assertEquals(0, queue.inFlightSize(), "In-flight queue should be empty after ack");

        queue.shutdown();

        System.out.println("TEST 2 PASSED");
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

    private void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ". Expected=" + expected + ", Actual=" + actual);
        }
    }

    private void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ". Expected=" + expected + ", Actual=" + actual);
        }
    }

    @FunctionalInterface
    private interface Check {
        boolean evaluate();
    }
}