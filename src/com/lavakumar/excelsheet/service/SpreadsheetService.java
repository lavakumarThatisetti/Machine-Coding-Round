package com.lavakumar.excelsheet.service;

import com.lavakumar.excelsheet.DisplayFormatter;
import com.lavakumar.excelsheet.cellcontent.CellContent;
import com.lavakumar.excelsheet.cellcontent.FormulaCellContent;
import com.lavakumar.excelsheet.cellcontent.LiteralCellContent;
import com.lavakumar.excelsheet.expression.Expression;
import com.lavakumar.excelsheet.model.Cell;
import com.lavakumar.excelsheet.model.CellAddress;
import com.lavakumar.excelsheet.model.EvaluationContext;
import com.lavakumar.excelsheet.model.Spreadsheet;
import com.lavakumar.excelsheet.parser.FormulaParser;

import java.math.BigDecimal;
import java.util.*;

public class SpreadsheetService {
    private final Spreadsheet spreadsheet = new Spreadsheet();
    private final FormulaParser formulaParser = new FormulaParser();

    public void setCell(String rawAddress, String rawInput) {
        CellAddress address = CellAddress.parse(rawAddress);
        validateInput(rawInput);

        CellContent content = buildContent(rawInput.trim());
        spreadsheet.putCell(address, new Cell(address, rawInput.trim(), content));
    }

    public void resetCell(String rawAddress) {
        CellAddress address = CellAddress.parse(rawAddress);
        spreadsheet.removeCell(address);
    }

    public BigDecimal getCellNumericValue(String rawAddress) {
        CellAddress address = CellAddress.parse(rawAddress);
        EvaluationContext context = new EvaluationContext(spreadsheet);
        return context.evaluateCell(address);
    }

    public String getCellDisplayValue(String rawAddress) {
        CellAddress address = CellAddress.parse(rawAddress);
        EvaluationContext context = new EvaluationContext(spreadsheet);
        BigDecimal value = context.evaluateCell(address);
        return DisplayFormatter.format(value);
    }

    public String printSheet(int maxRows, int maxCols) {
        if (maxRows <= 0 || maxCols <= 0) {
            throw new IllegalArgumentException("maxRows and maxCols must be > 0");
        }

        EvaluationContext context = new EvaluationContext(spreadsheet);
        StringBuilder sb = new StringBuilder();

        sb.append(padRight("", 12));
        for (int col = 1; col <= maxCols; col++) {
            sb.append("| ").append(padRight(CellAddress.columnNumberToName(col), 10));
        }
        sb.append('\n');

        for (int row = 1; row <= maxRows; row++) {
            sb.append(padRight(String.valueOf(row), 12));
            for (int col = 1; col <= maxCols; col++) {
                CellAddress address = new CellAddress(col, row);
                BigDecimal value = context.evaluateCell(address);
                sb.append("| ").append(padRight(DisplayFormatter.format(value), 10));
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    public Map<String, String> printUsedCells() {
        Map<String, String> result = new LinkedHashMap<>();
        EvaluationContext context = new EvaluationContext(spreadsheet);

        List<CellAddress> addresses = new ArrayList<>(spreadsheet.getAllAddresses());
        Collections.sort(addresses);

        for (CellAddress address : addresses) {
            result.put(address.toString(), DisplayFormatter.format(context.evaluateCell(address)));
        }
        return result;
    }

    private CellContent buildContent(String input) {
        if (input.startsWith("=")) {
            String formulaText = input.substring(1).trim();
            if (formulaText.isEmpty()) {
                throw new IllegalArgumentException("Formula cannot be empty");
            }
            Expression expression = formulaParser.parse(formulaText);
            return new FormulaCellContent(expression);
        }

        BigDecimal literalValue;
        try {
            literalValue = new BigDecimal(input);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Only numeric literals or formulas are supported: " + input);
        }

        return new LiteralCellContent(literalValue);
    }

    private void validateInput(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw new IllegalArgumentException("Cell input must not be blank");
        }
    }

    private String padRight(String value, int width) {
        if (value.length() >= width) {
            return value;
        }
        return value + " ".repeat(width - value.length());
    }
}
