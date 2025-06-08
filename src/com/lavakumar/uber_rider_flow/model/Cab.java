package com.lavakumar.uber_rider_flow.model;

public class Cab {
    private final String id;
    private final String driverName;
    private Location location;
    private boolean isAvailable;
    private VehicleType vehicleType;

    public Cab(String id, String driverName, Location location, VehicleType vehicleType) {
        this.id = id;
        this.driverName = driverName;
        this.location = location;
        this.vehicleType = vehicleType;
        this.isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void assignToRide() {
        this.isAvailable = false;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public String getDriverName() {
        return driverName;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}