package entity;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

public class PoolBooking {
    private final String id;
    private final String rideId;
    private final String riderId;
    private final Location pickup;
    private final Location destination;
    private final int seatsRequested;
    private final LocalDateTime createdAt;
    private final double estimatedFare;
    private final AtomicReference<PoolBookingStatus> status = new AtomicReference<>(PoolBookingStatus.CONFIRMED);

    public PoolBooking(
            String id,
            String rideId,
            String riderId,
            Location pickup,
            Location destination,
            int seatsRequested,
            double estimatedFare
    ) {
        this.id = id;
        this.rideId = rideId;
        this.riderId = riderId;
        this.pickup = pickup;
        this.destination = destination;
        this.seatsRequested = seatsRequested;
        this.createdAt = LocalDateTime.now();
        this.estimatedFare = estimatedFare;
    }

    public void cancel() {
        if (!status.compareAndSet(PoolBookingStatus.CONFIRMED, PoolBookingStatus.CANCELLED)) {
            throw new IllegalStateException("Booking cannot be cancelled from " + status.get());
        }
    }

    public void complete() {
        if (!status.compareAndSet(PoolBookingStatus.CONFIRMED, PoolBookingStatus.COMPLETED)) {
            throw new IllegalStateException("Booking cannot complete from " + status.get());
        }
    }

    public String getId() {
        return id;
    }

    public String getRideId() {
        return rideId;
    }

    public String getRiderId() {
        return riderId;
    }

    public Location getPickup() {
        return pickup;
    }

    public Location getDestination() {
        return destination;
    }

    public int getSeatsRequested() {
        return seatsRequested;
    }

    public double getEstimatedFare() {
        return estimatedFare;
    }

    public PoolBookingStatus getStatus() {
        return status.get();
    }
}
