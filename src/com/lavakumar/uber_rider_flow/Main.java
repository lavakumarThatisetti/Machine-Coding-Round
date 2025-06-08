package com.lavakumar.uber_rider_flow;

import com.lavakumar.uber_rider_flow.model.Booking;
import com.lavakumar.uber_rider_flow.model.Location;
import com.lavakumar.uber_rider_flow.model.Rider;
import com.lavakumar.uber_rider_flow.model.VehicleFareEstimate;
import com.lavakumar.uber_rider_flow.model.VehicleType;
import com.lavakumar.uber_rider_flow.service.BookingService;
import com.lavakumar.uber_rider_flow.service.CabService;
import com.lavakumar.uber_rider_flow.service.RiderService;
import com.lavakumar.uber_rider_flow.strategy.PricingStrategy;
import com.lavakumar.uber_rider_flow.strategy.VehicleTypePricingStrategy;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RiderService riderService = new RiderService();
        CabService cabService = new CabService();
        PricingStrategy pricingStrategy = new VehicleTypePricingStrategy();
        BookingService bookingService = new BookingService(cabService, pricingStrategy);

        // Step 1: Rider registers and sets current location
        Rider rider = riderService.registerRider("r1", "Alice");
        Location pickup = new Location(10, 10);
        rider.updateLocation(pickup);

        // Step 2: Rider enters destination
        Location destination = new Location(20, 20);

        // Register some cabs in the system
        cabService.registerCab("c1", "DriverOne", new Location(12, 10), VehicleType.SEDAN);
        cabService.registerCab("c2", "DriverTwo", new Location(11, 11), VehicleType.HATCHBACK);
        cabService.registerCab("c3", "DriverThree", new Location(5, 5), VehicleType.SUV); // far

        // Step 3: System shows estimated fare for each vehicle type
        System.out.println("\n📋 Fare Estimates:");
        List<VehicleFareEstimate> estimates = bookingService.showAvailableVehicleTypes(pickup, destination);
        for (VehicleFareEstimate e : estimates) {
            System.out.println(" - " + e);
        }

        // Step 4: Rider selects a vehicle type
        VehicleType chosenType = VehicleType.SEDAN; // Simulating user choice

        // Step 5: Book cab based on selected vehicle type
        Booking booking;
        try {
            booking = bookingService.bookCab(rider, chosenType, destination);
        } catch (RuntimeException e) {
            System.out.println("❌ Booking failed: " + e.getMessage());
            return;
        }

        // Step 6: Start the ride
        bookingService.startRide(booking);

        // Simulate ride in progress with animation
        System.out.println("🚕 Ride is in progress...");

        int totalSteps = 5;
        for (int i = 1; i <= totalSteps; i++) {
            try {
                System.out.print("⏳ Traveling");
                for (int j = 0; j < i; j++) {
                    System.out.print(".");
                }
                System.out.println(" (" + i + " sec)");
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("✅ Reached destination. Ending ride...");

        // Step 7: End the ride
        bookingService.endRide(booking);

        // Final summary
        System.out.println("\n🧾 Final Ride Summary:");
        booking.printSummary();
    }
}
