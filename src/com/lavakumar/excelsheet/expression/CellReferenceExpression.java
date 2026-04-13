package com.lavakumar.excelsheet.expression;

import com.lavakumar.excelsheet.model.CellAddress;
import com.lavakumar.excelsheet.model.EvaluationContext;

import java.math.BigDecimal;
import java.util.Objects;

public class CellReferenceExpression implements Expression {
    private final CellAddress address;

    public CellReferenceExpression(CellAddress address) {
        this.address = Objects.requireNonNull(address);
    }

    @Override
    public BigDecimal evaluate(EvaluationContext
                                           context) {
        return context.evaluateCell(address);
    }
}
