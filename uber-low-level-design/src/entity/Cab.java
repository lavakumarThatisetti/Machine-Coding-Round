package entity;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Cab {
    private final String id;
    private final VehicleType vehicleType;
    private volatile Location location;
    private final AtomicReference<CabStatus> status = new AtomicReference<>(CabStatus.AVAILABLE);
    private volatile String activeRideId;

    public Cab(String id, VehicleType vehicleType, Location location) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.location = location;
    }

    public boolean tryReserve(String rideId) {
        boolean success = status.compareAndSet(CabStatus.AVAILABLE, CabStatus.RESERVED);
        if (success) {
            this.activeRideId = rideId;
        }
        return success;
    }

    public void markOnTrip(String rideId) {
        validateRide(rideId);
        if (!status.compareAndSet(CabStatus.RESERVED, CabStatus.ON_TRIP)) {
            throw new IllegalStateException("Cab is not reserved");
        }
    }

    public void release(String rideId) {
        validateRide(rideId);
        activeRideId = null;
        status.set(CabStatus.AVAILABLE);
    }

    private void validateRide(String rideId) {
        if (!Objects.equals(activeRideId, rideId)) {
            throw new IllegalStateException("Cab not associated with ride: " + rideId);
        }
    }

    public String getId() {
        return id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Location getLocation() {
        return location;
    }

    public void updateLocation(Location location) {
        this.location = location;
    }

    public CabStatus getStatus() {
        return status.get();
    }

    public boolean isAvailable() {
        return status.get() == CabStatus.AVAILABLE;
    }
}
