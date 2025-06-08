package com.lavakumar.uber_with_driver_flow.pricing;

import com.lavakumar.uber_with_driver_flow.models.Location;
import com.lavakumar.uber_with_driver_flow.models.enums.VehicleType;

public class VehicleTypePricingStrategy implements PricingStrategy {
    @Override
    public double calculateFare(Location from, Location to, VehicleType vehicleType) {
        double baseRatePerKm = switch (vehicleType) {
            case HATCHBACK -> 8.0;
            case SEDAN -> 10.0;
            case SUV -> 12.5;
        };
        double distance = from.distanceTo(to);
        return baseRatePerKm * distance;
    }
}
