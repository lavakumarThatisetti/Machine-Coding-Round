package com.lavakumar.settledrivers.repository;

import com.lavakumar.settledrivers.model.DeliveryTrip;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TripRepository {
    private final ConcurrentHashMap<String, DeliveryTrip> tripsById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<DeliveryTrip>> tripsByDriver = new ConcurrentHashMap<>();

    public boolean exists(String tripId) {
        return tripsById.containsKey(tripId);
    }

    public void save(DeliveryTrip trip) {
        DeliveryTrip existing = tripsById.putIfAbsent(trip.getTripId(), trip);
        if (existing != null) {
            throw new IllegalArgumentException("Trip already exists: " + trip.getTripId());
        }

        tripsByDriver
                .computeIfAbsent(trip.getDriverId(), ignored -> new CopyOnWriteArrayList<>())
                .add(trip);
    }

    public Collection<DeliveryTrip> findAll() {
        return tripsById.values();
    }

    public List<DeliveryTrip> findByDriverId(String driverId) {
        return tripsByDriver.getOrDefault(driverId, new CopyOnWriteArrayList<>());
    }
}
