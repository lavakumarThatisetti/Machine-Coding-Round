package com.lavakumar.uber_with_driver_flow.models;

import com.lavakumar.uber_with_driver_flow.models.enums.VehicleType;

public class Cab {
    private String id;
    private String driverName;
    private Location location;
    private VehicleType vehicleType;
    private String carNumber;
    private boolean isAvailable;

    public Cab(String id, String driverName, Location location, VehicleType vehicleType, String carNumber) {
        this.id = id;
        this.driverName = driverName;
        this.location = location;
        this.vehicleType = vehicleType;
        this.carNumber = carNumber;
        this.isAvailable = true;
    }

    public boolean isAvailable() { return isAvailable; }
    public void assignToRide() { this.isAvailable = false; }
    public void setLocation(Location location) { this.location = location; }

    public Location getLocation() { return location; }
    public String getDriverName() { return driverName; }
    public VehicleType getVehicleType() { return vehicleType; }
    public String getCarNumber() { return carNumber; }
    public String getId() { return id; }
    public void markAvailable() { this.isAvailable = true; }
}
