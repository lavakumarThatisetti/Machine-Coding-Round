package com.lavakumar.settledrivers.model;

import java.math.BigDecimal;

public class Driver {
    private final String driverId;
    private final String driverName;
    private final BigDecimal hourlyRate;

    public Driver(String driverId, String driverName, BigDecimal hourlyRate) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.hourlyRate = hourlyRate;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
}
