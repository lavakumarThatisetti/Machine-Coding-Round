package com.lavakumar.expenserulenegine.rule;

import com.lavakumar.expenserulenegine.model.ExpenseCategory;
import com.lavakumar.expenserulenegine.model.ExpenseItem;
import com.lavakumar.expenserulenegine.model.RuleViolation;
import com.lavakumar.expenserulenegine.service.Money;
import com.lavakumar.expenserulenegine.service.ValidationContext;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class TripCategoryTotalLimitRule implements ExpenseRule {
    private final String ruleId;
    private final String description;
    private final ExpenseCategory targetCategory;
    private final BigDecimal maxTripCategoryTotal;

    public TripCategoryTotalLimitRule(String ruleId,
                               String description,
                               ExpenseCategory targetCategory,
                               BigDecimal maxTripCategoryTotal) {
        this.ruleId = Objects.requireNonNull(ruleId);
        this.description = Objects.requireNonNull(description);
        this.targetCategory = Objects.requireNonNull(targetCategory);
        this.maxTripCategoryTotal = Money.normalize(Objects.requireNonNull(maxTripCategoryTotal));
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

        if (expense.category() != targetCategory) {
            return Optional.empty();
        }

        BigDecimal current = context.currentApprovedTotalForTripAndCategory(expense.tripId(), expense.category());
        BigDecimal projected = Money.add(current, expense.amount());

        if (projected.compareTo(maxTripCategoryTotal) > 0) {
            return Optional.of(new RuleViolation(
                    ruleId,
                    description + " | currentApprovedCategoryTotal=" + Money.toDisplay(current) +
                            ", submitted=" + Money.toDisplay(expense.amount()) +
                            ", projected=" + Money.toDisplay(projected) +
                            ", maxAllowed=" + Money.toDisplay(maxTripCategoryTotal)
            ));
        }

        return Optional.empty();
    }
}

