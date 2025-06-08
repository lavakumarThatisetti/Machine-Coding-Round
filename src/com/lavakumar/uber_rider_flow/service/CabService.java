package com.lavakumar.uber_rider_flow.service;

import com.lavakumar.uber_rider_flow.model.Cab;
import com.lavakumar.uber_rider_flow.model.Location;
import com.lavakumar.uber_rider_flow.model.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class CabService {
    private final List<Cab> cabs = new ArrayList<>();

    public void registerCab(String id, String driverName, Location location, VehicleType vehicleType) {
        cabs.add(new Cab(id, driverName, location, vehicleType));
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

    // 🔧 New Method: Find nearest available cab of a given vehicle type
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
}