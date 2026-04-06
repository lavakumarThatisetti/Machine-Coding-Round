package service;

import entity.Location;
import entity.RideBooking;
import entity.VehicleType;

public class DefaultPricingStrategy implements PricingStrategy {
    @Override
    public double estimateFare(Location pickup, Location destination, VehicleType vehicleType) {
        double distance = pickup.distanceTo(destination);
        double base = switch (vehicleType) {
            case HATCHBACK -> 50;
            case SEDAN -> 80;
            case SUV -> 120;
        };
        return base + distance * 10;
    }

    @Override
    public double finalFare(RideBooking rideBooking) {
        return rideBooking.getEstimatedFare();
    }
}
