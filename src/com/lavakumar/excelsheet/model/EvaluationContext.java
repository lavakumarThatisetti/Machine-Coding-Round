package com.lavakumar.excelsheet.model;

import com.lavakumar.excelsheet.CircularReferenceException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

public class EvaluationContext {
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    private final Spreadsheet spreadsheet;
    private final Map<CellAddress, BigDecimal> memo = new HashMap<>();
    private final Map<CellAddress, VisitState> visitStateByCell = new HashMap<>();

    public EvaluationContext(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    public BigDecimal evaluateCell(CellAddress address) {
        BigDecimal cached = memo.get(address);
        if (cached != null) {
            return cached;
        }

        VisitState state = visitStateByCell.get(address);
        if (state == VisitState.VISITING) {
            throw new CircularReferenceException("Circular reference detected at cell: " + address);
        }
        if (state == VisitState.VISITED) {
            return memo.get(address);
        }

        visitStateByCell.put(address, VisitState.VISITING);

        Cell cell = spreadsheet.getCell(address);
        BigDecimal value;
        if (cell == null) {
            value = BigDecimal.ZERO;
        } else {
            value = cell.content().evaluate(this, address);
        }

        memo.put(address, value);
        visitStateByCell.put(address, VisitState.VISITED);
        return value;
    }

    public BigDecimal add(BigDecimal left, BigDecimal right) {
        return left.add(right, MATH_CONTEXT);
    }

    public BigDecimal subtract(BigDecimal left, BigDecimal right) {
        return left.subtract(right, MATH_CONTEXT);
    }

    public BigDecimal multiply(BigDecimal left, BigDecimal right) {
        return left.multiply(right, MATH_CONTEXT);
    }

    public BigDecimal divide(BigDecimal left, BigDecimal right) {
        if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left.divide(right, MATH_CONTEXT);
    }

    public BigDecimal negate(BigDecimal value) {
        return value.negate(MATH_CONTEXT);
    }
}
