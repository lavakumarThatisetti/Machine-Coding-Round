package com.lavakumar.settledrivers.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TripSettlement(
        String tripId,
        String driverId,
        Instant settledFrom,
        Instant settledUntil,
        BigDecimal amountPaid
) {}