package com.lavakumar.settledrivers.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TripView(String tripId,
                       Instant startTime,
                       Instant endTime,
                       BigDecimal totalTripCost,
                       BigDecimal paidAmount,
                       BigDecimal unpaidAmount) {
}
