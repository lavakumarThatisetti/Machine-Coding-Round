package com.lavakumar.excelsheet.expression;

import com.lavakumar.excelsheet.model.BinaryOperator;
import com.lavakumar.excelsheet.model.EvaluationContext;

import java.math.BigDecimal;
import java.util.Objects;

public class BinaryExpression implements Expression {
    private final Expression left;
    private final Expression right;
    private final BinaryOperator operator;

    public BinaryExpression(Expression left, Expression right, BinaryOperator operator) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.operator = Objects.requireNonNull(operator);
    }

    @Override
    public BigDecimal evaluate(EvaluationContext context) {
        BigDecimal leftValue = left.evaluate(context);
        BigDecimal rightValue = right.evaluate(context);

        return switch (operator) {
            case ADD -> context.add(leftValue, rightValue);
            case SUBTRACT -> context.subtract(leftValue, rightValue);
            case MULTIPLY -> context.multiply(leftValue, rightValue);
            case DIVIDE -> context.divide(leftValue, rightValue);
        };
    }
}
