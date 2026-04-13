package com.lavakumar.excelsheet;

import java.math.BigDecimal;

public class DisplayFormatter {
    public DisplayFormatter() {
    }

    public static String format(BigDecimal value) {
        BigDecimal normalized = value.stripTrailingZeros();
        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }
        return normalized.toPlainString();
    }
}