package service;

import entity.Location;
import entity.PoolRide;
import entity.VehicleType;

import java.util.List;

public interface PoolMatchStrategy {
    List<PoolRide> findCompatibleRides(
            Location pickup,
            Location destination,
            VehicleType vehicleType,
            int seatsRequested,
            List<PoolRide> activeRides
    );
}
