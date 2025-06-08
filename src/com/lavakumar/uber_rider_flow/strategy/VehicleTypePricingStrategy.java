package com.lavakumar.uber_rider_flow.strategy;

import com.lavakumar.uber_rider_flow.model.Location;
import com.lavakumar.uber_rider_flow.model.VehicleType;

public class VehicleTypePricingStrategy implements PricingStrategy {
    @Override
    public double calculateFare(Location from, Location to, VehicleType vehicleType) {
        double baseRatePerKm;
        switch (vehicleType) {
            case HATCHBACK -> baseRatePerKm = 8.0;
            case SEDAN -> baseRatePerKm = 10.0;
            case SUV -> baseRatePerKm = 12.5;
            default -> baseRatePerKm = 10.0;
        }
        double distance = from.distanceTo(to);
        return baseRatePerKm * distance;
    }
}
