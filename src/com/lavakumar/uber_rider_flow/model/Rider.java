package com.lavakumar.uber_rider_flow.model;

public class Rider {
    private final String id;
    private final String name;
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