package service;

import entity.Location;
import entity.VehicleType;

public class DefaultPricingStrategy implements PricingStrategy {
    @Override
    public double estimateFare(Location pickup, Location destination, VehicleType vehicleType, int seatsRequested) {
        double distance = pickup.distanceTo(destination);

        double base = switch (vehicleType) {
            case HATCHBACK -> 50;
            case SEDAN -> 80;
            case SUV -> 120;
        };

        // simple pooling logic: fare proportional to requested seats
        return (base + distance * 8) * seatsRequested;
    }
}
