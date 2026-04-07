package service;

import entity.Location;
import entity.VehicleType;

public interface PricingStrategy {
    double estimateFare(Location pickup, Location destination, VehicleType vehicleType, int seatsRequested);
}
