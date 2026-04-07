package model;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class Expense {
    private final String id;
    private final String title;
    private final BigDecimal totalAmount;
    private final String paidByUserId;
    private final String groupId; // nullable -> personal/direct expense
    private final List<Share> shares;
    private final Instant createdAt;

    public Expense(
            String id,
            String title,
            BigDecimal totalAmount,
            String paidByUserId,
            String groupId,
            List<Share> shares,
            Instant createdAt
    ) {
        this.id = requireNonBlank(id, "Expense id cannot be blank");
        this.title = requireNonBlank(title, "Expense title cannot be blank");
        this.totalAmount = requirePositive(totalAmount, "Expense total amount must be positive");
        this.paidByUserId = requireNonBlank(paidByUserId, "Paid by userId cannot be blank");
        this.groupId = groupId;
        this.shares = List.copyOf(Objects.requireNonNull(shares, "Shares cannot be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");

        if (this.shares.isEmpty()) {
            throw new IllegalArgumentException("Expense must have at least one share");
        }
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaidByUserId() {
        return paidByUserId;
    }

    public String getGroupId() {
        return groupId;
    }

    public List<Share> getShares() {
        return shares;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isGroupExpense() {
        return groupId != null;
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