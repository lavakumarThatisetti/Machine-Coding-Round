package repository;

import model.Rider;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRiderRepository implements RiderRepository {

    private final ConcurrentHashMap<String, Rider> riders = new ConcurrentHashMap<>();

    @Override
    public void save(Rider rider) {
        riders.put(rider.getId(), rider);
    }

    @Override
    public Optional<Rider> findById(String riderId) {
        return Optional.ofNullable(riders.get(riderId));
    }
}
