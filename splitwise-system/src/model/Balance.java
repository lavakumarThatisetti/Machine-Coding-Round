package model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @param groupId nullable means overall/non-group scoped
 */
public record Balance(String debtorUserId, String creditorUserId, BigDecimal amount, String groupId) {
    public Balance(String debtorUserId, String creditorUserId, BigDecimal amount, String groupId) {
        this.debtorUserId = requireNonBlank(debtorUserId, "Debtor userId cannot be blank");
        this.creditorUserId = requireNonBlank(creditorUserId, "Creditor userId cannot be blank");
        this.amount = requirePositiveOrZero(amount, "Balance amount cannot be null or negative");
        this.groupId = groupId;

        if (debtorUserId.equals(creditorUserId)) {
            throw new IllegalArgumentException("Debtor and creditor cannot be same");
        }
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static BigDecimal requirePositiveOrZero(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "debtorUserId='" + debtorUserId + '\'' +
                ", creditorUserId='" + creditorUserId + '\'' +
                ", amount=" + amount +
                ", groupId='" + groupId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Balance balance)) return false;
        return debtorUserId.equals(balance.debtorUserId)
                && creditorUserId.equals(balance.creditorUserId)
                && amount.compareTo(balance.amount) == 0
                && Objects.equals(groupId, balance.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(debtorUserId, creditorUserId, amount.stripTrailingZeros(), groupId);
    }
}
