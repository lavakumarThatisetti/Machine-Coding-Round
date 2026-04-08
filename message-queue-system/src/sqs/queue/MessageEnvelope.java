package sqs.queue;

import sqs.model.Message;

import java.util.Objects;

final class MessageEnvelope {
    private final Message message;
    private int receiveCount;

    MessageEnvelope(Message message) {
        this.message = Objects.requireNonNull(message, "message cannot be null");
        this.receiveCount = 0;
    }

    Message getMessage() {
        return message;
    }

    int getReceiveCount() {
        return receiveCount;
    }

    void incrementReceiveCount() {
        this.receiveCount++;
    }
}