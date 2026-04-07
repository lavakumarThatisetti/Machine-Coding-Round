package model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public final class Settlement {
    private final String id;
    private final String fromUserId;
    private final String toUserId;
    private final BigDecimal amount;
    private final String groupId; // nullable if overall settlement
    private final Instant createdAt;

    public Settlement(
            String id,
            String fromUserId,
            String toUserId,
            BigDecimal amount,
            String groupId,
            Instant createdAt
    ) {
        this.id = requireNonBlank(id, "Settlement id cannot be blank");
        this.fromUserId = requireNonBlank(fromUserId, "From userId cannot be blank");
        this.toUserId = requireNonBlank(toUserId, "To userId cannot be blank");
        this.amount = requirePositive(amount, "Settlement amount must be positive");
        this.groupId = groupId;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Settlement users must be different");
        }
    }

    public String getId() {
        return id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getGroupId() {
        return groupId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static BigDecimal requirePositive(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
