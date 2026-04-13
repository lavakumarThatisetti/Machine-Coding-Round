package com.lavakumar.expenserulenegine.rule;

import com.lavakumar.expenserulenegine.model.ExpenseCategory;
import com.lavakumar.expenserulenegine.model.ExpenseItem;
import com.lavakumar.expenserulenegine.model.RuleViolation;
import com.lavakumar.expenserulenegine.service.ValidationContext;

import java.util.Objects;
import java.util.Optional;

public class CategoryNotAllowedRule implements ExpenseRule {
    private final String ruleId;
    private final String description;
    private final ExpenseCategory blockedCategory;

    public CategoryNotAllowedRule(String ruleId, String description, ExpenseCategory blockedCategory) {
        this.ruleId = Objects.requireNonNull(ruleId);
        this.description = Objects.requireNonNull(description);
        this.blockedCategory = Objects.requireNonNull(blockedCategory);
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
        if (expense.category() == blockedCategory) {
            return Optional.of(new RuleViolation(
                    ruleId,
                    description
            ));
        }
        return Optional.empty();
    }
}
