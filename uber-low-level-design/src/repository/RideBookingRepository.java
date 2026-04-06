package repository;

import entity.RideBooking;

import java.util.List;
import java.util.Optional;

public interface RideBookingRepository {
    void save(RideBooking booking);
    Optional<RideBooking> findById(String rideId);
    List<RideBooking> findByRiderId(String riderId);
}
