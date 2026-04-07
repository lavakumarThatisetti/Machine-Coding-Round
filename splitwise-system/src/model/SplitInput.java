package model;

import java.math.BigDecimal;

public final class SplitInput {
    private final String userId;
    private final BigDecimal value;

    public SplitInput(String userId) {
        this.userId = requireNonBlank(userId, "Split userId cannot be blank");
        this.value = null;
    }

    public SplitInput(String userId, BigDecimal value) {
        this.userId = requireNonBlank(userId, "Split userId cannot be blank");
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getValue() {
        return value;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public String toString() {
        return "SplitInput{" +
                "userId='" + userId + '\'' +
                ", value=" + value +
                '}';
    }
}