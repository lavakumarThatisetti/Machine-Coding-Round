package service;

import entity.Location;
import entity.RideBooking;
import entity.VehicleType;

public interface PricingStrategy {
    double estimateFare(Location pickup, Location destination, VehicleType vehicleType);
    double finalFare(RideBooking rideBooking);
}
