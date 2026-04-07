package repository;

import model.Rider;

import java.util.Optional;

public interface RiderRepository {
    void save(Rider rider);
    Optional<Rider> findById(String riderId);
}