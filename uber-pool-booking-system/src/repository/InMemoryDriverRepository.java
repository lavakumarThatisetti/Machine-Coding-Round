package repository;

import entity.Driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDriverRepository implements DriverRepository {

    private final ConcurrentHashMap<String, Driver> drivers = new ConcurrentHashMap<>();

    @Override
    public void save(Driver driver) {
        drivers.put(driver.getId(), driver);
    }

    @Override
    public Optional<Driver> findById(String driverId) {
        if (driverId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(drivers.get(driverId));
    }

    @Override
    public List<Driver> findAll() {
        return new ArrayList<>(drivers.values());
    }
}
