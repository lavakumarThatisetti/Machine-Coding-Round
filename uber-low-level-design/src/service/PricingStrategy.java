package service;

import model.Location;
import model.RideBooking;
import model.VehicleType;

public interface PricingStrategy {
    double estimateFare(Location pickup, Location destination, VehicleType vehicleType);
    double finalFare(RideBooking rideBooking);
}
