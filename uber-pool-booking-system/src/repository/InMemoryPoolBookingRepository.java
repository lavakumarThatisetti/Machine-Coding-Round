package repository;

import model.PoolBooking;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryPoolBookingRepository implements PoolBookingRepository {
    private final ConcurrentHashMap<String, PoolBooking> bookings = new ConcurrentHashMap<>();

    @Override
    public void save(PoolBooking booking) {
        bookings.put(booking.getId(), booking);
    }

    @Override
    public Optional<PoolBooking> findById(String bookingId) {
        if (bookingId == null) return Optional.empty();
        return Optional.ofNullable(bookings.get(bookingId));
    }

    @Override
    public List<PoolBooking> findByRideId(String rideId) {
        return bookings.values().stream()
                .filter(b -> b.getRideId().equals(rideId))
                .collect(Collectors.toList());
    }

    @Override
    public List<PoolBooking> findByRiderId(String riderId) {
        return bookings.values().stream()
                .filter(b -> b.getRiderId().equals(riderId))
                .collect(Collectors.toList());
    }
}
