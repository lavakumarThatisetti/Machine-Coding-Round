package sqs.service;

import sqs.model.Message;
import sqs.queue.AckMessageQueue;

import java.util.Objects;

public class AckProducerService {
    private final AckMessageQueue messageQueue;

    public AckProducerService(AckMessageQueue messageQueue) {
        this.messageQueue = Objects.requireNonNull(messageQueue, "messageQueue cannot be null");
    }

    public Message publish(String producerId, String payload) throws InterruptedException {
        validate(producerId, payload);

        Message message = Message.of(producerId, payload);
        messageQueue.publish(message);
        return message;
    }

    private void validate(String producerId, String payload) {
        if (producerId == null || producerId.isBlank()) {
            throw new IllegalArgumentException("producerId cannot be blank");
        }
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload cannot be blank");
        }
    }
}