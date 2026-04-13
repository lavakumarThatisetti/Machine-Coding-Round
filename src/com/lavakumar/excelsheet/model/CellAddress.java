package com.lavakumar.excelsheet.model;

import com.lavakumar.excelsheet.ExcelSheetDemo;

import java.util.Objects;

public class CellAddress implements Comparable<CellAddress> {
    private final int column;
    private final int row;

    public CellAddress(int column, int row) {
        if (column <= 0) {
            throw new IllegalArgumentException("column must be > 0");
        }
        if (row <= 0) {
            throw new IllegalArgumentException("row must be > 0");
        }
        this.column = column;
        this.row = row;
    }

    public static CellAddress parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Cell address must not be blank");
        }

        String value = raw.trim().toUpperCase();
        int index = 0;

        while (index < value.length() && Character.isLetter(value.charAt(index))) {
            index++;
        }

        if (index == 0 || index == value.length()) {
            throw new IllegalArgumentException("Invalid cell address: " + raw);
        }

        String colPart = value.substring(0, index);
        String rowPart = value.substring(index);

        for (char ch : rowPart.toCharArray()) {
            if (!Character.isDigit(ch)) {
                throw new IllegalArgumentException("Invalid cell address: " + raw);
            }
        }

        int column = columnNameToNumber(colPart);
        int row = Integer.parseInt(rowPart);

        return new CellAddress(column, row);
    }

    public static int columnNameToNumber(String columnName) {
        int result = 0;
        for (char ch : columnName.toUpperCase().toCharArray()) {
            if (ch < 'A' || ch > 'Z') {
                throw new IllegalArgumentException("Invalid column name: " + columnName);
            }
            result = result * 26 + (ch - 'A' + 1);
        }
        return result;
    }

    public static String columnNumberToName(int columnNumber) {
        if (columnNumber <= 0) {
            throw new IllegalArgumentException("columnNumber must be > 0");
        }

        StringBuilder sb = new StringBuilder();
        int value = columnNumber;

        while (value > 0) {
            value--;
            sb.append((char) ('A' + (value % 26)));
            value /= 26;
        }

        return sb.reverse().toString();
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public int compareTo(CellAddress other) {
        int rowCompare = Integer.compare(this.row, other.row);
        if (rowCompare != 0) {
            return rowCompare;
        }
        return Integer.compare(this.column, other.column);
    }

    @Override
    public String toString() {
        return columnNumberToName(column) + row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellAddress that)) return false;
        return column == that.column && row == that.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }
}
