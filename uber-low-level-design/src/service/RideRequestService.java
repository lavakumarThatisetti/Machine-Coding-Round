package service;

import model.*;
import repository.RideBookingRepository;
import repository.RiderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RideRequestService {

    private final RiderRepository riderRepository;
    private final RideBookingRepository bookingRepository;
    private final PricingStrategy pricingStrategy;
    private final DriverNotificationService notificationService;
    private final DriverMatchingEngine driverMatchingEngine;

    // riderId -> activeRideId
    private final ConcurrentHashMap<String, String> riderActiveRides = new ConcurrentHashMap<>();

    public RideRequestService(
            RiderRepository riderRepository,
            RideBookingRepository bookingRepository,
            PricingStrategy pricingStrategy,
            DriverNotificationService notificationService, DriverMatchingEngine driverMatchingEngine
    ) {
        this.riderRepository = riderRepository;
        this.bookingRepository = bookingRepository;
        this.pricingStrategy = pricingStrategy;
        this.notificationService = notificationService;
        this.driverMatchingEngine = driverMatchingEngine;
    }

    public RideBooking requestRide(String riderId, Location destination, VehicleType vehicleType) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found: " + riderId));

        String rideId = UUID.randomUUID().toString();

        boolean riderLocked = riderActiveRides.putIfAbsent(riderId, rideId) == null;
        if (!riderLocked) {
            throw new IllegalStateException("Rider already has active ride/request");
        }

        try {
            double estimatedFare = pricingStrategy.estimateFare(rider.getCurrentLocation(), destination, vehicleType);

            RideBooking booking = new RideBooking(
                    rideId,
                    riderId,
                    rider.getCurrentLocation(),
                    destination,
                    vehicleType,
                    estimatedFare
            );

            bookingRepository.save(booking);

            List<Driver> candidates = driverMatchingEngine.findCandidates(rider.getCurrentLocation(), vehicleType);

            List<Driver> successfullyNotifiedDrivers = new ArrayList<>();

            for (Driver driver : candidates) {
                if (driver.tryMarkNotified()) {
                    booking.addNotifiedDriver(driver.getId());
                    successfullyNotifiedDrivers.add(driver);
                }
            }

            bookingRepository.save(booking);

            notificationService.notifyDrivers(booking, successfullyNotifiedDrivers);

            return booking;

        } catch (RuntimeException ex) {
            riderActiveRides.remove(riderId, rideId);
            throw ex;
        }
    }

    public void clearRiderActiveRide(String riderId, String rideId) {
        riderActiveRides.remove(riderId, rideId);
    }
}
