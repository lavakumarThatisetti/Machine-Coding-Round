package repository;

import model.PoolRide;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPoolRideRepository implements PoolRideRepository {
    private final ConcurrentHashMap<String, PoolRide> rides = new ConcurrentHashMap<>();

    @Override
    public void save(PoolRide ride) {
        rides.put(ride.getId(), ride);
    }

    @Override
    public Optional<PoolRide> findById(String rideId) {
        if (rideId == null) return Optional.empty();
        return Optional.ofNullable(rides.get(rideId));
    }

    @Override
    public List<PoolRide> findAll() {
        return new ArrayList<>(rides.values());
    }
}
