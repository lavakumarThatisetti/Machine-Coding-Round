package com.lavakumar.uber_with_driver_flow.service;

import com.lavakumar.uber_with_driver_flow.models.Cab;
import com.lavakumar.uber_with_driver_flow.models.Location;
import com.lavakumar.uber_with_driver_flow.models.enums.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class CabService {
    private final List<Cab> cabs = new ArrayList<>();

    public void registerCab(String id, String driverName, Location location, VehicleType vehicleType, String carNumber) {
        cabs.add(new Cab(id, driverName, location, vehicleType, carNumber));
    }

    public List<Cab> findNearbyCabs(Location riderLocation, double maxDistance) {
        List<Cab> nearby = new ArrayList<>();
        for (Cab cab : cabs) {
            if (cab.isAvailable() && cab.getLocation().distanceTo(riderLocation) <= maxDistance) {
                nearby.add(cab);
            }
        }
        return nearby;
    }

    public Cab findNearestAvailableCab(Location riderLocation, VehicleType vehicleType) {
        Cab nearestCab = null;
        double minDistance = Double.MAX_VALUE;

        for (Cab cab : cabs) {
            if (cab.isAvailable() && cab.getVehicleType() == vehicleType) {
                double distance = cab.getLocation().distanceTo(riderLocation);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestCab = cab;
                }
            }
        }

        return nearestCab;
    }

    public List<Cab> findNearestAvailableCabs(Location riderLocation, VehicleType vehicleType) {
        double minDistance = Double.MAX_VALUE;
        List<Cab> nearestCabs = new ArrayList<>();
        for(Cab cab: cabs) {
            if(cab.isAvailable() && cab.getVehicleType() == vehicleType) {
                double distance = cab.getLocation().distanceTo(riderLocation);
                if(distance < minDistance) {
                    nearestCabs.add(cab);
                }
            }
        }
        return nearestCabs;
    }
}
