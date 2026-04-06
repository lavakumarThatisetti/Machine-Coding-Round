package service;

import entity.Driver;
import entity.Location;
import entity.VehicleType;

import java.util.List;

public interface MatchingStrategy {
    List<Driver> findCandidateDrivers(Location pickup, VehicleType vehicleType, List<Driver> availableDrivers);
}