package com.lavakumar.excelsheet.cellcontent;

import com.lavakumar.excelsheet.expression.Expression;
import com.lavakumar.excelsheet.model.CellAddress;
import com.lavakumar.excelsheet.model.EvaluationContext;

import java.math.BigDecimal;
import java.util.Objects;

public class FormulaCellContent implements CellContent {
    private final Expression expression;

    public FormulaCellContent(Expression expression) {
        this.expression = Objects.requireNonNull(expression);
    }

    @Override
    public BigDecimal evaluate(EvaluationContext context, CellAddress self) {
        return expression.evaluate(context);
    }
}
