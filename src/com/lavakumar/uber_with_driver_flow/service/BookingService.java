package com.lavakumar.uber_with_driver_flow.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lavakumar.uber_with_driver_flow.models.Booking;
import com.lavakumar.uber_with_driver_flow.models.Cab;
import com.lavakumar.uber_with_driver_flow.models.Location;
import com.lavakumar.uber_with_driver_flow.models.Rider;
import com.lavakumar.uber_with_driver_flow.models.enums.BookingStatus;
import com.lavakumar.uber_with_driver_flow.models.enums.VehicleType;
import com.lavakumar.uber_with_driver_flow.pricing.PricingStrategy;
import com.lavakumar.uber_with_driver_flow.utils.VehicleFareEstimate;

public class BookingService {
    private final CabService cabService;
    private final PricingStrategy pricingStrategy;

    public BookingService(CabService cabService, PricingStrategy pricingStrategy) {
        this.cabService = cabService;
        this.pricingStrategy = pricingStrategy;
    }

    public List<VehicleFareEstimate> showAvailableVehicleTypes(Location from, Location to) {
        List<VehicleFareEstimate> estimates = new ArrayList<>();
        for (VehicleType type : VehicleType.values()) {
            double fare = pricingStrategy.calculateFare(from, to, type);
            fare = Math.round(fare * 100.0) / 100.0;
            estimates.add(new VehicleFareEstimate(type, fare));
        }
        return estimates;
    }

    public Booking bookCab(Rider rider, VehicleType vehicleType, Location destination) {
        Cab cab = cabService.findNearestAvailableCab(rider.getCurrentLocation(), vehicleType);
        if (cab == null) {
            throw new RuntimeException("No available cab of type: " + vehicleType);
        }
        cab.assignToRide();
        Booking booking = new Booking(rider, cab, destination, pricingStrategy);
        return booking;
    }

    public void driverStartRide(Booking booking, String enteredOtp) {
        if (!booking.getOtp().equals(enteredOtp)) {
            System.out.println("❌ Invalid OTP! Ride cannot start.");
            return;
        }
        booking.setStatus(BookingStatus.STARTED);
        booking.setStartTime(LocalDateTime.now());
        System.out.println("✅ OTP verified. Ride started.");
    }

    public void driverEndRide(Booking booking) {
        if (booking.getStatus() != BookingStatus.STARTED) {
            System.out.println("❌ Cannot end ride. Ride hasn't started.");
            return;
        }
        booking.setStatus(BookingStatus.ENDED);
        booking.setEndTime(LocalDateTime.now());
        double finalFare = pricingStrategy.calculateFare(
            booking.getPickupLocation(), booking.getDestination(), booking.getCab().getVehicleType()
        );
        booking.setFare(Math.round(finalFare * 100.0) / 100.0);
        System.out.println("🏁 Ride ended by driver.");
    }
}
