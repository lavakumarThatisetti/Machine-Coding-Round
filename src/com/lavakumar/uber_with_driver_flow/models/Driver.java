package com.lavakumar.uber_with_driver_flow.models;

public class Driver {
    private String id;
    private String name;

    public Driver(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
