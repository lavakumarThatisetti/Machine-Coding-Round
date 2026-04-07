package service;

import entity.*;
import repository.DriverRepository;
import repository.PoolBookingRepository;
import repository.PoolRideRepository;

import java.util.List;

public class PoolRideLifecycleService {
    private final PoolRideRepository poolRideRepository;
    private final PoolBookingRepository poolBookingRepository;
    private final DriverRepository driverRepository;
    private final PoolBookingService poolBookingService;

    public PoolRideLifecycleService(
            PoolRideRepository poolRideRepository,
            PoolBookingRepository poolBookingRepository,
            DriverRepository driverRepository,
            PoolBookingService poolBookingService
    ) {
        this.poolRideRepository = poolRideRepository;
        this.poolBookingRepository = poolBookingRepository;
        this.driverRepository = driverRepository;
        this.poolBookingService = poolBookingService;
    }

    public void cancelBooking(String bookingId) {
        PoolBooking booking = poolBookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        PoolRide ride = poolRideRepository.findById(booking.getRideId())
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + booking.getRideId()));

        if (ride.getStatus() != PoolRideStatus.OPEN_FOR_MATCHING) {
            throw new IllegalStateException("Booking can be cancelled only before ride starts");
        }

        booking.cancel();
        ride.releaseSeats(booking.getSeatsRequested());
        poolBookingService.clearRiderActiveBooking(booking.getRiderId(), booking.getId());

        List<PoolBooking> allBookings = poolBookingRepository.findByRideId(ride.getId());
        boolean allCancelled = allBookings.stream()
                .allMatch(b -> b.getStatus() == PoolBookingStatus.CANCELLED);

        if (allCancelled) {
            ride.markCancelled();

            Driver driver = driverRepository.findById(ride.getDriverId())
                    .orElseThrow(() -> new IllegalStateException("Driver not found for ride " + ride.getId()));

            driver.getCab().releaseToAvailable();
            driver.release();
        }
    }

    public void startRide(String rideId) {
        PoolRide ride = poolRideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + rideId));

        if (ride.getBookingIds().isEmpty()) {
            throw new IllegalStateException("Cannot start ride without any bookings");
        }

        Driver driver = driverRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new IllegalStateException("Driver not found: " + ride.getDriverId()));

        ride.markInProgress();
        driver.getCab().markOnTrip();
    }

    public void completeRide(String rideId) {
        PoolRide ride = poolRideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + rideId));

        if (ride.getStatus() != PoolRideStatus.IN_PROGRESS
                && ride.getStatus() != PoolRideStatus.OPEN_FOR_MATCHING) {
            throw new IllegalStateException("Ride can complete only from OPEN_FOR_MATCHING or IN_PROGRESS");
        }

        List<PoolBooking> rideBookings = poolBookingRepository.findByRideId(rideId);

        for (PoolBooking booking : rideBookings) {
            if (booking.getStatus() == PoolBookingStatus.CONFIRMED) {
                booking.complete();
                poolBookingService.clearRiderActiveBooking(booking.getRiderId(), booking.getId());
            }
        }

        ride.markCompleted();

        Driver driver = driverRepository.findById(ride.getDriverId())
                .orElseThrow(() -> new IllegalStateException("Driver not found: " + ride.getDriverId()));

        driver.getCab().releaseToAvailable();
        driver.release();
    }
}
