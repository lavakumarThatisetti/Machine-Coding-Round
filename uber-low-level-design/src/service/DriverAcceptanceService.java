package service;

import entity.Driver;
import entity.RideBooking;
import repository.DriverRepository;
import repository.RideBookingRepository;

public class DriverAcceptanceService {

    private final DriverRepository driverRepository;
    private final RideBookingRepository bookingRepository;

    public DriverAcceptanceService(
            DriverRepository driverRepository,
            RideBookingRepository bookingRepository
    ) {
        this.driverRepository = driverRepository;
        this.bookingRepository = bookingRepository;
    }

    public boolean acceptRide(String driverId, String rideId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + driverId));

        RideBooking booking = bookingRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + rideId));

        // Step 1: driver must be assignable
        if (!driver.tryAssignToRide(rideId)) {
            return false;
        }

        // Step 2: cab must be reservable
        boolean cabReserved = driver.getCab().tryReserve(rideId);
        if (!cabReserved) {
            driver.releaseRide(rideId);
            return false;
        }

        // Step 3: booking must still be REQUESTED
        boolean accepted = booking.tryAccept(driver.getId(), driver.getCab().getId());
        if (!accepted) {
            driver.getCab().release(rideId);
            driver.releaseRide(rideId);
            return false;
        }

        resetOtherNotifiedDrivers(booking, driverId);

        return true;
    }

    private void resetOtherNotifiedDrivers(RideBooking booking, String winningDriverId) {
        for (String notifiedDriverId : booking.getNotifiedDriverIds()) {
            if (!notifiedDriverId.equals(winningDriverId)) {
                driverRepository.findById(notifiedDriverId)
                        .ifPresent(Driver::resetFromNotified);
            }
        }
    }
}
