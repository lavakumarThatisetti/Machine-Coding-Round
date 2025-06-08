package com.lavakumar.uber_with_driver_flow.utils;

import com.lavakumar.uber_with_driver_flow.models.enums.VehicleType;

public class VehicleFareEstimate {
    private VehicleType vehicleType;
    private double estimatedFare;

    public VehicleFareEstimate(VehicleType vehicleType, double estimatedFare) {
        this.vehicleType = vehicleType;
        this.estimatedFare = estimatedFare;
    }

    @Override
    public String toString() {
        return vehicleType + " → ₹" + estimatedFare;
    }
}
