package repository;

import entity.PoolBooking;

import java.util.List;
import java.util.Optional;

public interface PoolBookingRepository {
    void save(PoolBooking booking);
    Optional<PoolBooking> findById(String bookingId);
    List<PoolBooking> findByRideId(String rideId);
    List<PoolBooking> findByRiderId(String riderId);
}
