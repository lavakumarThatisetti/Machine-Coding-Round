package com.lavakumar.uber_rider_flow.service;

import com.lavakumar.uber_rider_flow.model.Booking;
import com.lavakumar.uber_rider_flow.model.BookingStatus;
import com.lavakumar.uber_rider_flow.model.Cab;
import com.lavakumar.uber_rider_flow.model.Location;
import com.lavakumar.uber_rider_flow.model.Rider;
import com.lavakumar.uber_rider_flow.model.VehicleFareEstimate;
import com.lavakumar.uber_rider_flow.model.VehicleType;
import com.lavakumar.uber_rider_flow.strategy.PricingStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            double roundedFare = BigDecimal.valueOf(fare)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            estimates.add(new VehicleFareEstimate(type, roundedFare));
        }
        return estimates;
    }

    public Booking bookCab(Rider rider, VehicleType vehicleType, Location destination) {
        Cab cab = cabService.findNearestAvailableCab(rider.getCurrentLocation(), vehicleType);
        if (cab == null) throw new RuntimeException("No available cab of type " + vehicleType);
        cab.assignToRide();
        Booking booking = new Booking(rider, cab, destination, pricingStrategy);
        booking.setStatus(BookingStatus.CREATED);
        return booking;
    }

    public void startRide(Booking booking) {
        booking.setStatus(BookingStatus.STARTED);
        booking.setRideStartTime(LocalDateTime.now());
        System.out.println("🚕 Ride Started...");
    }

    public void endRide(Booking booking) {
        booking.setStatus(BookingStatus.ENDED);
        booking.setRideEndTime(LocalDateTime.now());
        double finalFare = pricingStrategy.calculateFare(
                booking.getPickupLocation(),
                booking.getDestination(),
                booking.getCab().getVehicleType()
        );
        booking.setFare(finalFare);
        System.out.println("🏁 Ride Ended.");
    }
}

