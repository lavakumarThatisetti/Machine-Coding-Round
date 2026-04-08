package simple.simulation;

import simple.model.Message;
import simple.service.ConsumerService;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerTask implements Runnable {
    private final String consumerId;
    private final ConsumerService consumerService;
    private final long processingDelayMillis;
    private final CountDownLatch startLatch;
    private final AtomicInteger consumedCount;
    private final Set<String> consumedMessageIds;

    public ConsumerTask(String consumerId,
                        ConsumerService consumerService,
                        long processingDelayMillis,
                        CountDownLatch startLatch,
                        AtomicInteger consumedCount,
                        Set<String> consumedMessageIds) {
        this.consumerId = Objects.requireNonNull(consumerId, "consumerId cannot be null");
        this.consumerService = Objects.requireNonNull(consumerService, "consumerService cannot be null");
        this.processingDelayMillis = processingDelayMillis;
        this.startLatch = Objects.requireNonNull(startLatch, "startLatch cannot be null");
        this.consumedCount = Objects.requireNonNull(consumedCount, "consumedCount cannot be null");
        this.consumedMessageIds = Objects.requireNonNull(consumedMessageIds, "consumedMessageIds cannot be null");
    }

    @Override
    public void run() {
        try {
            startLatch.await();

            while (true) {
                Message message = consumerService.consumeNext();
                if (message == null) {
                    break;
                }

                boolean added = consumedMessageIds.add(message.getMessageId());
                if (!added) {
                    throw new IllegalStateException("Duplicate consumption detected for messageId=" + message.getMessageId());
                }

                consumedCount.incrementAndGet();
                consumerService.process(consumerId, message);

                if (processingDelayMillis > 0) {
                    Thread.sleep(processingDelayMillis);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("[Consumer=%s] Interrupted%n", consumerId);
        }
    }
}