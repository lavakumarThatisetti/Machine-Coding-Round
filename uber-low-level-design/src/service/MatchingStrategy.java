package service;

import model.Driver;
import model.Location;
import model.VehicleType;

import java.util.List;

public interface MatchingStrategy {
    List<Driver> findCandidateDrivers(Location pickup, VehicleType vehicleType, List<Driver> availableDrivers);
}