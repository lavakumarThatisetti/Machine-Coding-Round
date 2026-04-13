package com.lavakumar.excelsheet.expression;

import java.math.BigDecimal;

public interface Expression {
    BigDecimal evaluate(EvaluationContext context);
}
