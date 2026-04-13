package com.lavakumar.excelsheet.expression;

import java.math.BigDecimal;
import java.util.Objects;

public class UnaryMinusExpression implements Expression {
    private final Expression expression;

    public UnaryMinusExpression(Expression expression) {
        this.expression = Objects.requireNonNull(expression);
    }

    @Override
    public BigDecimal evaluate(EvaluationContext context) {
        return context.negate(expression.evaluate(context));
    }
}
