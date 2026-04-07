package service;

import model.Location;
import model.PoolRide;
import model.PoolRideStatus;
import model.VehicleType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SimplePoolMatchStrategy implements PoolMatchStrategy {
    private static final double PICKUP_RADIUS_THRESHOLD = 5.0;
    private static final double DESTINATION_RADIUS_THRESHOLD = 8.0;

    @Override
    public List<PoolRide> findCompatibleRides(
            Location pickup,
            Location destination,
            VehicleType vehicleType,
            int seatsRequested,
            List<PoolRide> activeRides
    ) {
        return activeRides.stream()
                .filter(ride -> ride.getVehicleType() == vehicleType)
                .filter(ride -> ride.getStatus() == PoolRideStatus.OPEN_FOR_MATCHING)
                .filter(ride -> ride.getAvailableSeats() >= seatsRequested)
                .filter(ride -> ride.getAnchorPickup().distanceTo(pickup) <= PICKUP_RADIUS_THRESHOLD)
                .filter(ride -> ride.getAnchorDestination().distanceTo(destination) <= DESTINATION_RADIUS_THRESHOLD)
                .sorted(Comparator.comparingInt(PoolRide::getAvailableSeats))
                .collect(Collectors.toList());
    }
}
