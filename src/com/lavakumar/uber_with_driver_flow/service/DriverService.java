package com.lavakumar.uber_with_driver_flow.service;

import java.util.HashMap;
import java.util.Map;

import com.lavakumar.uber_with_driver_flow.models.Driver;
import com.lavakumar.uber_with_driver_flow.models.Location;

public class DriverService {
    private Map<String, Driver> drivers = new HashMap<>();

    public Driver registerDriver(String id, String name) {
        Driver driver = new Driver(id, name);
        drivers.put(id, driver);
        return driver;
    }

    public Driver getDriver(String id) {
        return drivers.get(id);
    }

    public void triggerRideAcceptance(String Driver, Double fare, Location dropLocation){
        // Send Notification
    }
}
