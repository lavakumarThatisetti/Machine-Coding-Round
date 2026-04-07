package model;

import java.math.BigDecimal;
import java.util.Objects;

public record Share(String userId, BigDecimal amount) {
    public Share(String userId, BigDecimal amount) {
        this.userId = requireNonBlank(userId, "Share userId cannot be blank");
        this.amount = requireNonNegative(amount, "Share amount cannot be null or negative");
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public String toString() {
        return "Share{" +
                "userId='" + userId + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Share)) return false;
        Share share = (Share) o;
        return userId.equals(share.userId) && amount.compareTo(share.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, amount.stripTrailingZeros());
    }
}