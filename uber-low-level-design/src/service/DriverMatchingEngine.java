package service;

import model.Driver;
import model.Location;
import model.VehicleType;
import repository.DriverRepository;

import java.util.List;

public class DriverMatchingEngine {

    private final DriverRepository driverRepository;
    private final MatchingStrategy matchingStrategy;

    public DriverMatchingEngine(DriverRepository driverRepository, MatchingStrategy matchingStrategy) {
        this.driverRepository = driverRepository;
        this.matchingStrategy = matchingStrategy;
    }

    public List<Driver> findCandidates(Location pickup, VehicleType vehicleType) {
        return matchingStrategy.findCandidateDrivers(
                pickup,
                vehicleType,
                driverRepository.findAll()
        );
    }
}
