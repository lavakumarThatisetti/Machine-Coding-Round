package model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Driver {
    private final String id;
    private final String name;
    private final Cab cab;
    private final AtomicReference<DriverStatus> status = new AtomicReference<>(DriverStatus.AVAILABLE);
    private volatile String activeRideId;

    public Driver(String id, String name, Cab cab) {
        this.id = id;
        this.name = name;
        this.cab = cab;
    }

    public boolean tryMarkNotified() {
        return status.compareAndSet(DriverStatus.AVAILABLE, DriverStatus.NOTIFIED);
    }

    public void resetFromNotified() {
        status.compareAndSet(DriverStatus.NOTIFIED, DriverStatus.AVAILABLE);
    }

    public boolean tryAssignToRide(String rideId) {
        DriverStatus current = status.get();
        if (current != DriverStatus.NOTIFIED && current != DriverStatus.AVAILABLE) {
            return false;
        }

        boolean success = status.compareAndSet(current, DriverStatus.BUSY);
        if (success) {
            this.activeRideId = rideId;
        }
        return success;
    }

    public void releaseRide(String rideId) {
        if (!Objects.equals(activeRideId, rideId)) {
            throw new IllegalStateException("Driver not associated with ride " + rideId);
        }
        activeRideId = null;
        status.set(DriverStatus.AVAILABLE);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Cab getCab() {
        return cab;
    }

    public DriverStatus getStatus() {
        return status.get();
    }

    public boolean isAvailable() {
        return status.get() == DriverStatus.AVAILABLE;
    }
}
