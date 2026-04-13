package com.lavakumar.excelsheet.expression;

import com.lavakumar.excelsheet.model.EvaluationContext;

import java.math.BigDecimal;

public interface Expression {
    BigDecimal evaluate(EvaluationContext context);
}
