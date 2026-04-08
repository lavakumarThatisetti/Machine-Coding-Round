package sqs.model;

import java.util.Objects;

public final class Delivery {
    private final String receiptHandle;
    private final Message message;
    private final int receiveCount;

    public Delivery(String receiptHandle, Message message, int receiveCount) {
        this.receiptHandle = Objects.requireNonNull(receiptHandle, "receiptHandle cannot be null");
        this.message = Objects.requireNonNull(message, "message cannot be null");
        this.receiveCount = receiveCount;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public Message getMessage() {
        return message;
    }

    public int getReceiveCount() {
        return receiveCount;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "receiptHandle='" + receiptHandle + '\'' +
                ", message=" + message +
                ", receiveCount=" + receiveCount +
                '}';
    }
}