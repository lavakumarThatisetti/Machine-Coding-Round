package com.lavakumar.expenserulenegine.rule;

import com.lavakumar.expenserulenegine.model.ExpenseItem;
import com.lavakumar.expenserulenegine.model.RuleViolation;
import com.lavakumar.expenserulenegine.service.ValidationContext;

import java.util.Optional;

public interface ExpenseRule {
    String ruleId();
    String description();
    Optional<RuleViolation> validate(ExpenseItem expense, ValidationContext context);
}
