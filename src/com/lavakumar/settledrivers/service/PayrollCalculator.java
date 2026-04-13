package com.lavakumar.settledrivers.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class PayrollCalculator {
    private static final BigDecimal SECONDS_PER_HOUR = new BigDecimal("3600");

    private PayrollCalculator() {
    }

    public static BigDecimal calculateCost(BigDecimal hourlyRate, Instant startTime, Instant endTime) {
        Objects.requireNonNull(hourlyRate, "hourlyRate must not be null");
        Objects.requireNonNull(startTime, "startTime must not be null");
        Objects.requireNonNull(endTime, "endTime must not be null");

        if (!endTime.isAfter(startTime)) {
            return Money.zero();
        }

        long seconds = Duration.between(startTime, endTime).getSeconds();
        if (seconds <= 0) {
            return Money.zero();
        }

        BigDecimal durationInSeconds = BigDecimal.valueOf(seconds);
        BigDecimal cost = hourlyRate
                .multiply(durationInSeconds)
                .divide(SECONDS_PER_HOUR, Money.INTERNAL_SCALE, RoundingMode.HALF_UP);

        return Money.normalize(cost);
    }
}