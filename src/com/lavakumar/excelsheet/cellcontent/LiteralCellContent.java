package com.lavakumar.excelsheet.cellcontent;

import com.lavakumar.excelsheet.model.CellAddress;
import com.lavakumar.excelsheet.model.EvaluationContext;

import java.math.BigDecimal;

public class LiteralCellContent implements CellContent {
    private final BigDecimal value;

    public LiteralCellContent(BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal evaluate(EvaluationContext context, CellAddress self) {
        return value;
    }
}
