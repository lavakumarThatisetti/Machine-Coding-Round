package com.lavakumar.expenserulenegine.rule;

import com.lavakumar.expenserulenegine.model.ExpenseCategory;
import com.lavakumar.expenserulenegine.model.ExpenseItem;
import com.lavakumar.expenserulenegine.model.RuleViolation;
import com.lavakumar.expenserulenegine.service.Money;
import com.lavakumar.expenserulenegine.service.ValidationContext;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class MaxAmountPerExpenseRule implements ExpenseRule {
    private final String ruleId;
    private final String description;
    private final ExpenseCategory targetCategory;
    private final BigDecimal maxAmount;

    public MaxAmountPerExpenseRule(String ruleId,
                            String description,
                            ExpenseCategory targetCategory,
                            BigDecimal maxAmount) {
        this.ruleId = Objects.requireNonNull(ruleId);
        this.description = Objects.requireNonNull(description);
        this.targetCategory = targetCategory;
        this.maxAmount = Money.normalize(Objects.requireNonNull(maxAmount));
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
        if (targetCategory != null && expense.category() != targetCategory) {
            return Optional.empty();
        }

        if (expense.amount().compareTo(maxAmount) > 0) {
            return Optional.of(new RuleViolation(
                    ruleId,
                    description + " | submitted=" + Money.toDisplay(expense.amount()) +
                            ", maxAllowed=" + Money.toDisplay(maxAmount)
            ));
        }

        return Optional.empty();
    }
}
