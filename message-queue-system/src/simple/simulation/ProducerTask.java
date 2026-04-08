package simple.simulation;

import simple.model.Message;
import simple.service.ProducerService;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerTask implements Runnable {
    private final String producerId;
    private final ProducerService producerService;
    private final int messageCount;
    private final long delayMillis;
    private final CountDownLatch startLatch;
    private final AtomicInteger producedCount;

    public ProducerTask(String producerId,
                        ProducerService producerService,
                        int messageCount,
                        long delayMillis,
                        CountDownLatch startLatch,
                        AtomicInteger producedCount) {
        this.producerId = Objects.requireNonNull(producerId, "producerId cannot be null");
        this.producerService = Objects.requireNonNull(producerService, "producerService cannot be null");
        this.messageCount = messageCount;
        this.delayMillis = delayMillis;
        this.startLatch = Objects.requireNonNull(startLatch, "startLatch cannot be null");
        this.producedCount = Objects.requireNonNull(producedCount, "producedCount cannot be null");
    }

    @Override
    public void run() {
        try {
            startLatch.await();

            for (int i = 1; i <= messageCount; i++) {
                String payload = "message-" + i + " from " + producerId;
                Message message = producerService.publish(producerId, payload);
                producedCount.incrementAndGet();

                System.out.printf(
                        "[Producer=%s] Published messageId=%s payload=%s%n",
                        producerId,
                        message.getMessageId(),
                        message.getPayload()
                );

                if (delayMillis > 0) {
                    Thread.sleep(delayMillis);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("[Producer=%s] Interrupted%n", producerId);
        }
    }
}