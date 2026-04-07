package entity;

import java.util.concurrent.atomic.AtomicReference;

public class Driver {
    private final String id;
    private final String name;
    private final Cab cab;
    private final AtomicReference<DriverStatus> status = new AtomicReference<>(DriverStatus.AVAILABLE);

    public Driver(String id, String name, Cab cab) {
        this.id = id;
        this.name = name;
        this.cab = cab;
    }

    public String getId() {
        return id;
    }

    public Cab getCab() {
        return cab;
    }

    public DriverStatus getStatus() {
        return status.get();
    }

    public boolean tryAssignPoolRide() {
        return status.compareAndSet(DriverStatus.AVAILABLE, DriverStatus.BUSY);
    }

    public void release() {
        status.set(DriverStatus.AVAILABLE);
    }

}
