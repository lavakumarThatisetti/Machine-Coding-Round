package service;

import entity.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NearestDriverMatchingStrategy implements MatchingStrategy {

    private final int maxCandidates;

    public NearestDriverMatchingStrategy(int maxCandidates) {
        this.maxCandidates = maxCandidates;
    }

    @Override
    public List<Driver> findCandidateDrivers(Location pickup, VehicleType vehicleType, List<Driver> availableDrivers) {
        return availableDrivers.stream()
                .filter(driver -> driver.getCab().getVehicleType() == vehicleType)
                .filter(driver -> driver.getStatus() == DriverStatus.AVAILABLE)
                .filter(driver -> driver.getCab().getStatus() == CabStatus.AVAILABLE)
                .sorted(Comparator.comparingDouble(d -> d.getCab().getLocation().distanceTo(pickup)))
                .limit(maxCandidates)
                .collect(Collectors.toList());
    }
}
