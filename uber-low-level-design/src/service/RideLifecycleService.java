package service;

import entity.*;
import repository.DriverRepository;
import repository.RideBookingRepository;

public class RideLifecycleService {

    private final RideBookingRepository bookingRepository;
    private final DriverRepository driverRepository;
    private final PricingStrategy pricingStrategy;
    private final RideRequestService rideRequestService;

    public RideLifecycleService(
            RideBookingRepository bookingRepository,
            DriverRepository driverRepository,
            PricingStrategy pricingStrategy,
            RideRequestService rideRequestService
    ) {
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.pricingStrategy = pricingStrategy;
        this.rideRequestService = rideRequestService;
    }

    public void startRide(String rideId) {
        RideBooking booking = bookingRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + rideId));

        if (booking.getStatus() != RideStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Cannot start ride. Expected ACCEPTED but found " + booking.getStatus()
            );
        }

        String driverId = booking.getDriverId();
        if (driverId == null) {
            throw new IllegalStateException("Cannot start ride without assigned driver");
        }

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalStateException("Assigned driver not found: " + driverId));

        booking.markInProgress();
        driver.getCab().markOnTrip(rideId);
    }

    public void completeRide(String rideId) {
        RideBooking booking = bookingRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + rideId));

        if (booking.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Cannot complete ride. Expected IN_PROGRESS but found " + booking.getStatus()
            );
        }

        String driverId = booking.getDriverId();
        if (driverId == null) {
            throw new IllegalStateException("Cannot complete ride without assigned driver");
        }

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalStateException("Assigned driver not found: " + driverId));

        double fare = pricingStrategy.finalFare(booking);

        booking.markCompleted(fare);
        driver.getCab().release(rideId);
        driver.releaseRide(rideId);
        rideRequestService.clearRiderActiveRide(booking.getRiderId(), booking.getId());
    }

    public void cancelRide(String rideId) {
        RideBooking booking = bookingRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + rideId));

        RideStatus currentStatus = booking.getStatus();
        if (currentStatus != RideStatus.REQUESTED && currentStatus != RideStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Cannot cancel ride from status " + currentStatus
            );
        }

        booking.markCancelled();

        String driverId = booking.getDriverId();
        if (driverId != null) {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new IllegalStateException("Assigned driver not found: " + driverId));

            if (driver.getCab().getStatus() != CabStatus.AVAILABLE) {
                driver.getCab().release(rideId);
            }
            if (driver.getStatus() != DriverStatus.AVAILABLE) {
                driver.releaseRide(rideId);
            }
        }

        rideRequestService.clearRiderActiveRide(booking.getRiderId(), booking.getId());
    }
}
