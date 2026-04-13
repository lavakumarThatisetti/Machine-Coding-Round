package com.lavakumar.expenserulenegine.rule;

import com.lavakumar.expenserulenegine.model.ExpenseItem;
import com.lavakumar.expenserulenegine.model.RuleViolation;
import com.lavakumar.expenserulenegine.service.Money;
import com.lavakumar.expenserulenegine.service.ValidationContext;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class TripTotalLimitRule implements ExpenseRule {
    private final String ruleId;
    private final String description;
    private final BigDecimal maxTripTotal;

    public TripTotalLimitRule(String ruleId, String description, BigDecimal maxTripTotal) {
        this.ruleId = Objects.requireNonNull(ruleId);
        this.description = Objects.requireNonNull(description);
        this.maxTripTotal = Money.normalize(Objects.requireNonNull(maxTripTotal));
    }

    @Override
    public String ruleId() {
        return ruleId;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Optional<RuleViolation> validate(ExpenseItem expense, ValidationContext context) {
        if (expense.tripId() == null || expense.tripId().isBlank()) {
            return Optional.empty();
        }

        BigDecimal current = context.currentApprovedTotalForTrip(expense.tripId());
        BigDecimal projected = Money.add(current, expense.amount());

        if (projected.compareTo(maxTripTotal) > 0) {
            return Optional.of(new RuleViolation(
                    ruleId,
                    description + " | currentApproved=" + Money.toDisplay(current) +
                            ", submitted=" + Money.toDisplay(expense.amount()) +
                            ", projected=" + Money.toDisplay(projected) +
                            ", maxAllowed=" + Money.toDisplay(maxTripTotal)
            ));
        }

        return Optional.empty();
    }
}
