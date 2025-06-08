package com.lavakumar.uber_with_driver_flow.pricing;

import com.lavakumar.uber_with_driver_flow.models.Location;
import com.lavakumar.uber_with_driver_flow.models.enums.VehicleType;

public interface PricingStrategy {
    double calculateFare(Location from, Location to, VehicleType vehicleType);
}
