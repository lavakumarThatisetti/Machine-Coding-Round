package repository;

import entity.PoolRide;

import java.util.List;
import java.util.Optional;

public interface PoolRideRepository {
    void save(PoolRide ride);
    Optional<PoolRide> findById(String rideId);
    List<PoolRide> findAll();
}
