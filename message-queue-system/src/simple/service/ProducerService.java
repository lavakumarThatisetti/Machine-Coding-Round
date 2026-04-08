package simple.service;

import simple.model.Message;
import simple.queue.SimpleMessageQueue;

import java.util.Objects;

public class ProducerService {
    private final SimpleMessageQueue simpleMessageQueue;

    public ProducerService(SimpleMessageQueue simpleMessageQueue) {
        this.simpleMessageQueue = Objects.requireNonNull(simpleMessageQueue, "messageQueue cannot be null");
    }

    public Message publish(String producerId, String payload) throws InterruptedException {
        validate(producerId, payload);

        Message message = Message.of(producerId, payload);
        simpleMessageQueue.publish(message);

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
