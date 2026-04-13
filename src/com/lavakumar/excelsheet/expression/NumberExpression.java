package com.lavakumar.excelsheet.expression;

import com.lavakumar.excelsheet.model.EvaluationContext;

import java.math.BigDecimal;

public class NumberExpression implements Expression {
    private final BigDecimal value;

    public NumberExpression(BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal evaluate(EvaluationContext context) {
        return value;
    }
}
