package service;

import model.*;
import repository.DriverRepository;
import repository.PoolBookingRepository;
import repository.PoolRideRepository;
import repository.RiderRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PoolBookingService {
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;
    private final PoolRideRepository poolRideRepository;
    private final PoolBookingRepository poolBookingRepository;
    private final PoolMatchStrategy poolMatchStrategy;
    private final PricingStrategy pricingStrategy;

    // riderId -> activeBookingId
    private final ConcurrentHashMap<String, String> riderActiveBookings = new ConcurrentHashMap<>();

    public PoolBookingService(
            RiderRepository riderRepository,
            DriverRepository driverRepository,
            PoolRideRepository poolRideRepository,
            PoolBookingRepository poolBookingRepository,
            PoolMatchStrategy poolMatchStrategy,
            PricingStrategy pricingStrategy
    ) {
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
        this.poolRideRepository = poolRideRepository;
        this.poolBookingRepository = poolBookingRepository;
        this.poolMatchStrategy = poolMatchStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public PoolBooking requestPoolRide(
            String riderId,
            Location pickup,
            Location destination,
            VehicleType vehicleType,
            int seatsRequested
    ) {
        validateRequest(riderId, pickup, destination, seatsRequested);

        String requestMarker = UUID.randomUUID().toString();
        boolean riderLocked = riderActiveBookings.putIfAbsent(riderId, requestMarker) == null;
        if (!riderLocked) {
            throw new IllegalStateException("Rider already has an active pool booking");
        }

        try {
            riderRepository.findById(riderId)
                    .orElseThrow(() -> new IllegalArgumentException("Rider not found: " + riderId));

            List<PoolRide> candidateRides = poolMatchStrategy.findCompatibleRides(
                    pickup,
                    destination,
                    vehicleType,
                    seatsRequested,
                    poolRideRepository.findAll()
            );

            for (PoolRide ride : candidateRides) {
                if (ride.tryReserveSeats(seatsRequested)) {
                    double fare = pricingStrategy.estimateFare(pickup, destination, vehicleType, seatsRequested);

                    PoolBooking booking = new PoolBooking(
                            UUID.randomUUID().toString(),
                            ride.getId(),
                            riderId,
                            pickup,
                            destination,
                            seatsRequested,
                            fare
                    );

                    ride.addBooking(booking.getId());
                    poolBookingRepository.save(booking);

                    riderActiveBookings.put(riderId, booking.getId());
                    return booking;
                }
            }

            // no compatible ride found, create a new pool ride
            Driver selectedDriver = findAndReserveAvailableDriver(vehicleType);

            PoolRide newRide = new PoolRide(
                    UUID.randomUUID().toString(),
                    selectedDriver.getId(),
                    selectedDriver.getCab().getId(),
                    vehicleType,
                    selectedDriver.getCab().getSeatCapacity(),
                    pickup,
                    destination
            );

            boolean reserved = newRide.tryReserveSeats(seatsRequested);
            if (!reserved) {
                releaseDriverAndCab(selectedDriver);
                throw new IllegalStateException("Failed to reserve seats in newly created pool ride");
            }

            double fare = pricingStrategy.estimateFare(pickup, destination, vehicleType, seatsRequested);

            PoolBooking booking = new PoolBooking(
                    UUID.randomUUID().toString(),
                    newRide.getId(),
                    riderId,
                    pickup,
                    destination,
                    seatsRequested,
                    fare
            );

            newRide.addBooking(booking.getId());

            poolRideRepository.save(newRide);
            poolBookingRepository.save(booking);

            riderActiveBookings.put(riderId, booking.getId());
            return booking;

        } catch (RuntimeException ex) {
            riderActiveBookings.remove(riderId, requestMarker);
            throw ex;
        }
    }

    private void validateRequest(
            String riderId,
            Location pickup,
            Location destination,
            int seatsRequested
    ) {
        if (riderId == null || riderId.isBlank()) {
            throw new IllegalArgumentException("Invalid riderId");
        }
        if (pickup == null || destination == null) {
            throw new IllegalArgumentException("Pickup and destination are required");
        }
        if (seatsRequested <= 0) {
            throw new IllegalArgumentException("Seats requested must be positive");
        }
    }

    private Driver findAndReserveAvailableDriver(VehicleType vehicleType) {
        List<Driver> candidates = driverRepository.findAll().stream()
                .filter(d -> d.getCab().getVehicleType() == vehicleType)
                .toList();

        for (Driver driver : candidates) {
            if (driver.tryAssignPoolRide()) {
                boolean cabReserved = driver.getCab().tryReserveForPool();
                if (cabReserved) {
                    return driver;
                }
                driver.release();
            }
        }

        throw new IllegalStateException("No available driver/cab found for new pooled ride");
    }

    private void releaseDriverAndCab(Driver driver) {
        driver.getCab().releaseToAvailable();
        driver.release();
    }

    public void clearRiderActiveBooking(String riderId, String bookingId) {
        riderActiveBookings.remove(riderId, bookingId);
    }
}
