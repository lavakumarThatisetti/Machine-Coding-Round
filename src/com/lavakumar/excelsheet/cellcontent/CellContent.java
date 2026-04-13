package com.lavakumar.excelsheet.cellcontent;

import com.lavakumar.excelsheet.model.CellAddress;
import com.lavakumar.excelsheet.expression.EvaluationContext;

import java.math.BigDecimal;

public interface CellContent {
    BigDecimal evaluate(EvaluationContext context, CellAddress self);
}
