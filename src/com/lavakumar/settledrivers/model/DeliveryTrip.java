package com.lavakumar.settledrivers.model;

import java.math.BigDecimal;
import java.time.Instant;

public class DeliveryTrip {
    private final String tripId;
    private final String driverId;
    private final Instant startTime;
    private final Instant endTime;
    private final BigDecimal totalCost;

    // Meaning: work in [startTime, settledUntil) is already paid
    private Instant settledUntil;

    public DeliveryTrip(String tripId,
                        String driverId,
                        Instant startTime,
                        Instant endTime,
                        BigDecimal totalCost) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCost = totalCost;
        this.settledUntil = startTime;
    }

    public String getTripId() {
        return tripId;
    }

    public String getDriverId() {
        return driverId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public Instant getSettledUntil() {
        return settledUntil;
    }

    public void settleUntil(Instant newSettledUntil) {
        if (newSettledUntil.isBefore(settledUntil)) {
            throw new IllegalArgumentException("Cannot move settledUntil backwards");
        }
        if (newSettledUntil.isAfter(endTime)) {
            throw new IllegalArgumentException("Cannot settle beyond trip endTime");
        }
        this.settledUntil = newSettledUntil;
    }

    public boolean hasUnpaidWork() {
        return settledUntil.isBefore(endTime);
    }

}
