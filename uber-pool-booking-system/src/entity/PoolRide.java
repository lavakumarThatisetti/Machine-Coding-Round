package entity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PoolRide {
    private final String id;
    private final String driverId;
    private final String cabId;
    private final VehicleType vehicleType;
    private final int capacity;
    private final AtomicInteger availableSeats;
    private final AtomicReference<PoolRideStatus> status = new AtomicReference<>(PoolRideStatus.OPEN_FOR_MATCHING);
    private final CopyOnWriteArrayList<String> bookingIds = new CopyOnWriteArrayList<>();

    // simplified "route window"
    private final Location anchorPickup;
    private final Location anchorDestination;

    public PoolRide(
            String id,
            String driverId,
            String cabId,
            VehicleType vehicleType,
            int capacity,
            Location anchorPickup,
            Location anchorDestination
    ) {
        this.id = id;
        this.driverId = driverId;
        this.cabId = cabId;
        this.vehicleType = vehicleType;
        this.capacity = capacity;
        this.availableSeats = new AtomicInteger(capacity);
        this.anchorPickup = anchorPickup;
        this.anchorDestination = anchorDestination;
    }

    public boolean tryReserveSeats(int seats) {
        while (true) {
            PoolRideStatus currentStatus = status.get();
            if (currentStatus != PoolRideStatus.OPEN_FOR_MATCHING) {
                return false;
            }

            int current = availableSeats.get();
            if (current < seats) {
                return false;
            }

            if (availableSeats.compareAndSet(current, current - seats)) {
                return true;
            }
        }
    }

    public void releaseSeats(int seats) {
        int updated = availableSeats.addAndGet(seats);
        if (updated > capacity) {
            throw new IllegalStateException("Available seats cannot exceed capacity");
        }
    }

    public void addBooking(String bookingId) {
        bookingIds.add(bookingId);
    }

    public void markInProgress() {
        if (!status.compareAndSet(PoolRideStatus.OPEN_FOR_MATCHING, PoolRideStatus.IN_PROGRESS)) {
            throw new IllegalStateException("Ride cannot start from " + status.get());
        }
    }

    public void markCompleted() {
        PoolRideStatus current = status.get();
        if (current != PoolRideStatus.IN_PROGRESS && current != PoolRideStatus.OPEN_FOR_MATCHING) {
            throw new IllegalStateException("Ride cannot complete from " + current);
        }
        status.set(PoolRideStatus.COMPLETED);
    }

    public void markCancelled() {
        PoolRideStatus current = status.get();
        if (current == PoolRideStatus.COMPLETED) {
            throw new IllegalStateException("Completed ride cannot be cancelled");
        }
        status.set(PoolRideStatus.CANCELLED);
    }

    public boolean isFull() {
        return availableSeats.get() == 0;
    }

    public String getId() {
        return id;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getCabId() {
        return cabId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableSeats() {
        return availableSeats.get();
    }

    public PoolRideStatus getStatus() {
        return status.get();
    }

    public List<String> getBookingIds() {
        return List.copyOf(bookingIds);
    }

    public Location getAnchorPickup() {
        return anchorPickup;
    }

    public Location getAnchorDestination() {
        return anchorDestination;
    }
}
