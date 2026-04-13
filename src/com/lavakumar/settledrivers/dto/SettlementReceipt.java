package com.lavakumar.settledrivers.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SettlementReceipt(
        Instant requestedUpToTime,
        BigDecimal paidThisRun,
        BigDecimal totalPaidSoFar,
        BigDecimal totalUnpaidRemaining,
        List<TripSettlement> tripSettlements
) {}
