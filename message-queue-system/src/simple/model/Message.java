package simple.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

// Immutable model - thread safe by design, no partial updates, read only
public final class Message {
    private final String messageId;
    private final String producerId;
    private final String payload;
    private final Instant createdAt;

    public Message(String messageId, String producerId, String payload, Instant createdAt) {
        this.messageId = Objects.requireNonNull(messageId, "messageId cannot be null");
        this.producerId = Objects.requireNonNull(producerId, "producerId cannot be null");
        this.payload = Objects.requireNonNull(payload, "payload cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    public static Message of(String producerId, String payload) {
        return new Message(
                UUID.randomUUID().toString(),
                producerId,
                payload,
                Instant.now()
        );
    }

    public String getMessageId() {
        return messageId;
    }

    public String getProducerId() {
        return producerId;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", producerId='" + producerId + '\'' +
                ", payload='" + payload + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}