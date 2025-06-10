package com.lavakumar.uber_with_driver_flow.pricing;

import com.lavakumar.uber_with_driver_flow.models.Location;
import com.lavakumar.uber_with_driver_flow.models.enums.VehicleType;

public class SurgePricingStrategy implements PricingStrategy {
    private final int demandCount;
    // private DemandService demandService

    public SurgePricingStrategy(int demandCount) {
        this.demandCount = demandCount;
    }

    @Override
    public double calculateFare(Location from, Location to, VehicleType vehicleType) {
        double baseRatePerKm = switch (vehicleType) {
            case HATCHBACK -> 8.0;
            case SEDAN -> 10.0;
            case SUV -> 12.5;
        };
        double distance = from.distanceTo(to);
        // demandService.getSurgeMultiplier(from, to);
        double surgeMultiplier = 1 + (demandCount / 10.0); // e.g., 10+ riders = 2x fare
        return baseRatePerKm * distance * surgeMultiplier;
    }
}
