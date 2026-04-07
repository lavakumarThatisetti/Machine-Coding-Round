package entity;

import java.util.concurrent.atomic.AtomicReference;

public class Cab {
    private final String id;
    private final VehicleType vehicleType;
    private volatile Location location;
    private final int seatCapacity;
    private final AtomicReference<CabStatus> status = new AtomicReference<>(CabStatus.AVAILABLE);

    public Cab(String id, VehicleType vehicleType, Location location, int seatCapacity) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.location = location;
        this.seatCapacity = seatCapacity;
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

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public CabStatus getStatus() {
        return status.get();
    }

    public boolean tryReserveForPool() {
        return status.compareAndSet(CabStatus.AVAILABLE, CabStatus.RESERVED_FOR_POOL);
    }

    public void markOnTrip() {
        if (!status.compareAndSet(CabStatus.RESERVED_FOR_POOL, CabStatus.ON_TRIP)) {
            throw new IllegalStateException("Cab cannot move to ON_TRIP from " + status.get());
        }
    }

    public void releaseToAvailable() {
        status.set(CabStatus.AVAILABLE);
    }
}

