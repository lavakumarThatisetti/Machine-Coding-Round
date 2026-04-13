package com.lavakumar.settledrivers.repository;

import com.lavakumar.settledrivers.model.Driver;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DriverRepository {
    private final ConcurrentHashMap<String, Driver> drivers = new ConcurrentHashMap<>();

    public boolean save(Driver driver) {
        return drivers.putIfAbsent(driver.getDriverId(), driver) == null;
    }

    public Optional<Driver> findById(String driverId) {
        return Optional.ofNullable(drivers.get(driverId));
    }

    public Collection<Driver> findAll() {
        return drivers.values();
    }
}
