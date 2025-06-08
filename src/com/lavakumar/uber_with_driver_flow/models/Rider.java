package com.lavakumar.uber_with_driver_flow.models;

public class Rider {
    private String id;
    private String name;
    private Location currentLocation;

    public Rider(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void updateLocation(Location location) {
        this.currentLocation = location;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
