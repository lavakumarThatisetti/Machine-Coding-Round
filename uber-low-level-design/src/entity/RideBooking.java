package entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class RideBooking {
    private final String id;
    private final String riderId;
    private final Location pickup;
    private final Location destination;
    private final VehicleType requestedVehicleType;
    private final LocalDateTime createdAt;

    private final AtomicReference<RideStatus> status = new AtomicReference<>(RideStatus.REQUESTED);
    private final List<String> notifiedDriverIds = new CopyOnWriteArrayList<>();

    private volatile String driverId;
    private volatile String cabId;
    private volatile LocalDateTime acceptedAt;
    private volatile LocalDateTime startedAt;
    private volatile LocalDateTime completedAt;
    private volatile double estimatedFare;
    private volatile double finalFare;

    public RideBooking(
            String id,
            String riderId,
            Location pickup,
            Location destination,
            VehicleType requestedVehicleType,
            double estimatedFare
    ) {
        this.id = id;
        this.riderId = riderId;
        this.pickup = pickup;
        this.destination = destination;
        this.requestedVehicleType = requestedVehicleType;
        this.createdAt = LocalDateTime.now();
        this.estimatedFare = estimatedFare;
    }

    public boolean tryAccept(String driverId, String cabId) {
        boolean success = status.compareAndSet(RideStatus.REQUESTED, RideStatus.ACCEPTED);
        if (success) {
            this.driverId = driverId;
            this.cabId = cabId;
            this.acceptedAt = LocalDateTime.now();
        }
        return success;
    }

    public void markInProgress() {
        if (!status.compareAndSet(RideStatus.ACCEPTED, RideStatus.IN_PROGRESS)) {
            throw new IllegalStateException("Ride cannot start from status: " + status.get());
        }
        this.startedAt = LocalDateTime.now();
    }

    public void markCompleted(double finalFare) {
        if (!status.compareAndSet(RideStatus.IN_PROGRESS, RideStatus.COMPLETED)) {
            throw new IllegalStateException("Ride cannot complete from status: " + status.get());
        }
        this.finalFare = finalFare;
        this.completedAt = LocalDateTime.now();
    }

    public void markCancelled() {
        RideStatus current = status.get();
        if (current == RideStatus.COMPLETED || current == RideStatus.CANCELLED) {
            throw new IllegalStateException("Ride cannot be cancelled from status: " + current);
        }
        status.set(RideStatus.CANCELLED);
    }

    public void addNotifiedDriver(String driverId) {
        notifiedDriverIds.add(driverId);
    }

    public List<String> getNotifiedDriverIds() {
        return List.copyOf(notifiedDriverIds);
    }

    public String getId() {
        return id;
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

    public VehicleType getRequestedVehicleType() {
        return requestedVehicleType;
    }

    public RideStatus getStatus() {
        return status.get();
    }

    public String getDriverId() {
        return driverId;
    }

    public String getCabId() {
        return cabId;
    }

    public double getEstimatedFare() {
        return estimatedFare;
    }

    public double getFinalFare() {
        return finalFare;
    }
}