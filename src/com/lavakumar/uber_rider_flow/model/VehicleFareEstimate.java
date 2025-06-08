package com.lavakumar.uber_rider_flow.model;

public class VehicleFareEstimate {
    private final VehicleType vehicleType;
    private final double estimatedFare;

    public VehicleFareEstimate(VehicleType vehicleType, double estimatedFare) {
        this.vehicleType = vehicleType;
        this.estimatedFare = estimatedFare;
    }

    @Override
    public String toString() {
        return vehicleType + " → ₹" + estimatedFare;
    }
}