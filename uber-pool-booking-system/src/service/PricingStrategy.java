package service;

import model.Location;
import model.VehicleType;

public interface PricingStrategy {
    double estimateFare(Location pickup, Location destination, VehicleType vehicleType, int seatsRequested);
}
