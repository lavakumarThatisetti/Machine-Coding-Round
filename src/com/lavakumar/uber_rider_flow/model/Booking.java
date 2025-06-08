package com.lavakumar.uber_rider_flow.model;

import com.lavakumar.uber_rider_flow.strategy.PricingStrategy;

import java.time.LocalDateTime;

public class Booking {
    private final Rider rider;
    private final Cab cab;
    private final Location pickupLocation;
    private final Location destination;
    private final LocalDateTime bookingTime;

    private LocalDateTime rideStartTime;

    private LocalDateTime rideEndTime;
    private double fare;

    private BookingStatus status;

    public Booking(Rider rider, Cab cab, Location destination, PricingStrategy strategy) {
        this.rider = rider;
        this.cab = cab;
        this.pickupLocation = rider.getCurrentLocation();
        this.destination = destination;
        this.bookingTime = LocalDateTime.now();
        this.fare = strategy.calculateFare(pickupLocation, destination, cab.getVehicleType());
    }

    public void printSummary() {
        System.out.println("✅ Booking Confirmed!");
        System.out.println("Rider: " + rider.getName());
        System.out.println("Driver: " + cab.getDriverName());
        System.out.println("Pickup: " + pickupLocation + " → Drop: " + destination);
        System.out.println("Vehicle: " + cab.getVehicleType());
        System.out.println("Fare: ₹" + fare);
        System.out.println("Time: " + bookingTime);
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Rider getRider() {
        return rider;
    }

    public Cab getCab() {
        return cab;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDestination() {
        return destination;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public LocalDateTime getRideStartTime() {
        return rideStartTime;
    }

    public void setRideStartTime(LocalDateTime rideStartTime) {
        this.rideStartTime = rideStartTime;
    }

    public LocalDateTime getRideEndTime() {
        return rideEndTime;
    }

    public void setRideEndTime(LocalDateTime rideEndTime) {
        this.rideEndTime = rideEndTime;
    }
}
