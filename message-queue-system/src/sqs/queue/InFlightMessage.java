package sqs.queue;

import java.util.Objects;

final class InFlightMessage {
    private final String receiptHandle;
    private final MessageEnvelope envelope;
    private final long visibilityDeadlineMillis;

    InFlightMessage(String receiptHandle, MessageEnvelope envelope, long visibilityDeadlineMillis) {
        this.receiptHandle = Objects.requireNonNull(receiptHandle, "receiptHandle cannot be null");
        this.envelope = Objects.requireNonNull(envelope, "envelope cannot be null");
        this.visibilityDeadlineMillis = visibilityDeadlineMillis;
    }

    String getReceiptHandle() {
        return receiptHandle;
    }

    MessageEnvelope getEnvelope() {
        return envelope;
    }

    long getVisibilityDeadlineMillis() {
        return visibilityDeadlineMillis;
    }

    boolean isExpired(long nowMillis) {
        return nowMillis >= visibilityDeadlineMillis;
    }
}