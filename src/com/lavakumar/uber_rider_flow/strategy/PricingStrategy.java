package com.lavakumar.uber_rider_flow.strategy;

import com.lavakumar.uber_rider_flow.model.Location;
import com.lavakumar.uber_rider_flow.model.VehicleType;

public interface PricingStrategy {
    double calculateFare(Location from, Location to, VehicleType vehicleType);
}
