package com.lavakumar.settledrivers.dto;

import java.math.BigDecimal;
import java.util.List;

public record DriverSummary(
        String driverId,
        String driverName,
        BigDecimal hourlyRate,
        BigDecimal totalAccrued,
        BigDecimal totalPaid,
        BigDecimal totalUnpaid,
        List<TripView> trips
) {}
