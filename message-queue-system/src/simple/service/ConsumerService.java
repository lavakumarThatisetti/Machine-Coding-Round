package simple.service;

import simple.model.Message;
import simple.queue.SimpleMessageQueue;

import java.util.Objects;

public class ConsumerService {
    private final SimpleMessageQueue simpleMessageQueue;

    public ConsumerService(SimpleMessageQueue simpleMessageQueue) {
        this.simpleMessageQueue = Objects.requireNonNull(simpleMessageQueue, "messageQueue cannot be null");
    }

    public Message consumeNext() throws InterruptedException {
        return simpleMessageQueue.consume();
    }

    public void process(String consumerId, Message message) {
        Objects.requireNonNull(consumerId, "consumerId cannot be null");
        Objects.requireNonNull(message, "message cannot be null");

        System.out.printf(
                "[Consumer=%s] Processed messageId=%s from producer=%s payload=%s%n",
                consumerId,
                message.getMessageId(),
                message.getProducerId(),
                message.getPayload()
        );
    }
}