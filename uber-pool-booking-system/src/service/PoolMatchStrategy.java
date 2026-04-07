package service;

import model.Location;
import model.PoolRide;
import model.VehicleType;

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
