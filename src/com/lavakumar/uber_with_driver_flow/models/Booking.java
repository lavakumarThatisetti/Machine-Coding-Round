package com.lavakumar.uber_with_driver_flow.models;

import com.lavakumar.uber_with_driver_flow.models.enums.BookingStatus;
import com.lavakumar.uber_with_driver_flow.pricing.PricingStrategy;

import java.time.LocalDateTime;

public class Booking {
    private Rider rider;
    private Cab cab;
    private Location pickupLocation;
    private Location destination;
    private BookingStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double fare;
    private String otp;

    public Booking(Rider rider, Cab cab, Location destination, PricingStrategy strategy) {
        this.rider = rider;
        this.cab = cab;
        this.pickupLocation = rider.getCurrentLocation();
        this.destination = destination;
        this.status = BookingStatus.CREATED;
        this.otp = generateOtp();
        this.fare = strategy.calculateFare(pickupLocation, destination, cab.getVehicleType());
    }

    private String generateOtp() {
        int otp = 1000 + (int)(Math.random() * 9000);
        return String.valueOf(otp);
    }

    public void setStatus(BookingStatus status) { this.status = status; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setFare(double fare) { this.fare = fare; }

    public Rider getRider() { return rider; }
    public Cab getCab() { return cab; }
    public Location getPickupLocation() { return pickupLocation; }
    public Location getDestination() { return destination; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public double getFare() { return fare; }
    public String getOtp() { return otp; }

    public void printSummary() {
        System.out.println("Rider: " + rider.getName());
        System.out.println("Driver: " + cab.getDriverName());
        System.out.println("Car: " + cab.getCarNumber() + " (" + cab.getVehicleType() + ")");
        System.out.println("Pickup: " + pickupLocation + " → Drop: " + destination);
        System.out.println("Fare: ₹" + fare);
        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + endTime);
    }
}
