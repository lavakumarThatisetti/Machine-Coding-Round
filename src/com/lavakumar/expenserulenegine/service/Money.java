package com.lavakumar.expenserulenegine.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {
    static final int INTERNAL_SCALE = 8;
    static final int DISPLAY_SCALE = 2;

    private Money() {
    }

    public static BigDecimal zero() {
        return BigDecimal.ZERO.setScale(INTERNAL_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal normalize(BigDecimal value) {
        return value.setScale(INTERNAL_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal add(BigDecimal left, BigDecimal right) {
        return normalize(left.add(right));
    }

    public static BigDecimal toDisplay(BigDecimal value) {
        return value.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);
    }
}
