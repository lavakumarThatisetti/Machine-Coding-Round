package repository;

import model.Driver;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    void save(Driver driver);
    Optional<Driver> findById(String driverId);
    List<Driver> findAll();
}