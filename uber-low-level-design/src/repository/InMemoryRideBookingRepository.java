package repository;

import model.RideBooking;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRideBookingRepository implements RideBookingRepository {

    private final ConcurrentHashMap<String, RideBooking> bookings = new ConcurrentHashMap<>();

    @Override
    public void save(RideBooking booking) {
        bookings.put(booking.getId(), booking);
    }

    @Override
    public Optional<RideBooking> findById(String rideId) {
        return Optional.ofNullable(bookings.get(rideId));
    }

    @Override
    public List<RideBooking> findByRiderId(String riderId) {
        return bookings.values().stream().
                filter(bookings  -> bookings.getRiderId().equals(riderId))
                .toList();
    }
}
