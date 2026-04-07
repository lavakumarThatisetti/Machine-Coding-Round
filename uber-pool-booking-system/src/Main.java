import model.*;
import repository.*;
import service.*;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Main demo = new Main();

        demo.testCreateNewPoolRideAndJoinExisting();
        demo.testCancelBookingBeforeRideStarts();
        demo.testStartAndCompletePoolRide();
        demo.testConcurrentLastSeatRace();
        demo.testConcurrentDuplicateRequestSameRider();
        demo.testNoCompatibleRideCreatesNewPoolRide();
    }

    // =========================================================
    // ======================= TEST CASES =======================
    // =========================================================

    private void testCreateNewPoolRideAndJoinExisting() {
        System.out.println("\n==============================================");
        System.out.println("TEST 1: Create New Pool Ride And Join Existing");
        System.out.println("==============================================");

        TestContext ctx = createContext();

        Rider rider1 = new Rider("R1", "Lava", new Location(10, 10));
        Rider rider2 = new Rider("R2", "Aman", new Location(10.5, 10.4));
        ctx.riderRepository.save(rider1);
        ctx.riderRepository.save(rider2);

        Driver driver = new Driver(
                "D1",
                "Driver-1",
                new Cab("C1", VehicleType.SEDAN, new Location(11, 11), 3)
        );
        ctx.driverRepository.save(driver);

        PoolBooking booking1 = ctx.poolBookingService.requestPoolRide(
                "R1",
                rider1.getCurrentLocation(),
                new Location(20, 20),
                VehicleType.SEDAN,
                1
        );

        PoolBooking booking2 = ctx.poolBookingService.requestPoolRide(
                "R2",
                rider2.getCurrentLocation(),
                new Location(20.2, 20.1),
                VehicleType.SEDAN,
                1
        );

        printPoolBooking(booking1, "Booking-1");
        printPoolBooking(booking2, "Booking-2");

        PoolRide ride1 = ctx.poolRideRepository.findById(booking1.getRideId()).orElseThrow();
        PoolRide ride2 = ctx.poolRideRepository.findById(booking2.getRideId()).orElseThrow();

        System.out.println("Ride-1 id = " + ride1.getId());
        System.out.println("Ride-2 id = " + ride2.getId());
        System.out.println("Available seats = " + ride1.getAvailableSeats());
        System.out.println("Booking count in ride = " + ride1.getBookingIds().size());

        assertCondition(ride1.getId().equals(ride2.getId()), "Both riders should join same pool ride");
        assertCondition(ride1.getAvailableSeats() == 1, "Available seats should reduce from 3 to 1");
    }

    private void testCancelBookingBeforeRideStarts() {
        System.out.println("\n======================================");
        System.out.println("TEST 2: Cancel Booking Before Start");
        System.out.println("======================================");

        TestContext ctx = createContext();

        Rider rider1 = new Rider("R10", "User-10", new Location(10, 10));
        Rider rider2 = new Rider("R11", "User-11", new Location(10.2, 10.1));
        ctx.riderRepository.save(rider1);
        ctx.riderRepository.save(rider2);

        Driver driver = new Driver(
                "D10",
                "Driver-10",
                new Cab("C10", VehicleType.SUV, new Location(11, 11), 4)
        );
        ctx.driverRepository.save(driver);

        PoolBooking b1 = ctx.poolBookingService.requestPoolRide(
                "R10", rider1.getCurrentLocation(), new Location(25, 25), VehicleType.SUV, 1
        );
        PoolBooking b2 = ctx.poolBookingService.requestPoolRide(
                "R11", rider2.getCurrentLocation(), new Location(25.3, 25.2), VehicleType.SUV, 1
        );

        PoolRide ride = ctx.poolRideRepository.findById(b1.getRideId()).orElseThrow();
        System.out.println("Available seats before cancel = " + ride.getAvailableSeats());

        ctx.poolRideLifecycleService.cancelBooking(b2.getId());

        PoolBooking updatedBooking = ctx.poolBookingRepository.findById(b2.getId()).orElseThrow();
        PoolRide updatedRide = ctx.poolRideRepository.findById(b1.getRideId()).orElseThrow();

        printPoolBooking(updatedBooking, "Cancelled booking");
        System.out.println("Available seats after cancel = " + updatedRide.getAvailableSeats());

        assertCondition(updatedBooking.getStatus() == PoolBookingStatus.CANCELLED, "Booking should be cancelled");
        assertCondition(updatedRide.getAvailableSeats() == 3, "Seat should be released back");
    }

    private void testStartAndCompletePoolRide() {
        System.out.println("\n======================================");
        System.out.println("TEST 3: Start And Complete Pool Ride");
        System.out.println("======================================");

        TestContext ctx = createContext();

        Rider rider1 = new Rider("R20", "User-20", new Location(1, 1));
        Rider rider2 = new Rider("R21", "User-21", new Location(1.2, 1.1));
        ctx.riderRepository.save(rider1);
        ctx.riderRepository.save(rider2);

        Driver driver = new Driver(
                "D20",
                "Driver-20",
                new Cab("C20", VehicleType.HATCHBACK, new Location(2, 2), 3)
        );
        ctx.driverRepository.save(driver);

        PoolBooking b1 = ctx.poolBookingService.requestPoolRide(
                "R20", rider1.getCurrentLocation(), new Location(9, 9), VehicleType.HATCHBACK, 1
        );
        PoolBooking b2 = ctx.poolBookingService.requestPoolRide(
                "R21", rider2.getCurrentLocation(), new Location(9.4, 9.2), VehicleType.HATCHBACK, 1
        );

        PoolRide ride = ctx.poolRideRepository.findById(b1.getRideId()).orElseThrow();
        printPoolRide(ride, "Before start");

        ctx.poolRideLifecycleService.startRide(ride.getId());
        PoolRide startedRide = ctx.poolRideRepository.findById(ride.getId()).orElseThrow();
        printPoolRide(startedRide, "After start");

        ctx.poolRideLifecycleService.completeRide(ride.getId());
        PoolRide completedRide = ctx.poolRideRepository.findById(ride.getId()).orElseThrow();
        printPoolRide(completedRide, "After complete");

        PoolBooking updatedB1 = ctx.poolBookingRepository.findById(b1.getId()).orElseThrow();
        PoolBooking updatedB2 = ctx.poolBookingRepository.findById(b2.getId()).orElseThrow();

        printPoolBooking(updatedB1, "Booking-1 after complete");
        printPoolBooking(updatedB2, "Booking-2 after complete");

        assertCondition(completedRide.getStatus() == PoolRideStatus.COMPLETED, "Ride should be completed");
        assertCondition(updatedB1.getStatus() == PoolBookingStatus.COMPLETED, "Booking-1 should be completed");
        assertCondition(updatedB2.getStatus() == PoolBookingStatus.COMPLETED, "Booking-2 should be completed");
    }

    private void testConcurrentLastSeatRace() throws Exception {
        System.out.println("\n======================================");
        System.out.println("TEST 4: Concurrent Last Seat Race");
        System.out.println("======================================");

        TestContext ctx = createContext();

        Rider rider1 = new Rider("R30", "User-30", new Location(10, 10));
        Rider rider2 = new Rider("R31", "User-31", new Location(10.1, 10.1));
        Rider rider3 = new Rider("R32", "User-32", new Location(10.2, 10.2));

        ctx.riderRepository.save(rider1);
        ctx.riderRepository.save(rider2);
        ctx.riderRepository.save(rider3);

        Driver driver = new Driver(
                "D30",
                "Driver-30",
                new Cab("C30", VehicleType.SEDAN, new Location(11, 11), 2)
        );
        ctx.driverRepository.save(driver);

        // first rider creates ride, now one seat left
        PoolBooking initialBooking = ctx.poolBookingService.requestPoolRide(
                "R30", rider1.getCurrentLocation(), new Location(20, 20), VehicleType.SEDAN, 1
        );

        PoolRide ride = ctx.poolRideRepository.findById(initialBooking.getRideId()).orElseThrow();
        printPoolRide(ride, "Before last-seat race");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        List<String> outcomes = Collections.synchronizedList(new ArrayList<>());

        submitPoolRequestTask(
                executor, ctx,
                "R31", rider2.getCurrentLocation(), new Location(20.1, 20.2), VehicleType.SEDAN,
                readyLatch, startLatch, doneLatch, outcomes
        );

        submitPoolRequestTask(
                executor, ctx,
                "R32", rider3.getCurrentLocation(), new Location(20.2, 20.3), VehicleType.SEDAN,
                readyLatch, startLatch, doneLatch, outcomes
        );

        readyLatch.await();
        System.out.println("Both last-seat contenders are ready. Starting race...");
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        System.out.println("Outcomes = " + outcomes);

        long sameRideSuccessCount = outcomes.stream()
                .filter(s -> s.startsWith("SUCCESS"))
                .filter(s -> s.contains("ride=" + ride.getId()))
                .count();

        PoolRide updatedRide = ctx.poolRideRepository.findById(ride.getId()).orElseThrow();
        printPoolRide(updatedRide, "After last-seat race");

        // exactly one should join this existing ride
        assertCondition(sameRideSuccessCount == 1, "Exactly one rider should get the last seat in the existing ride");
        assertCondition(updatedRide.getAvailableSeats() == 0, "Ride should become full");
    }

    private void testConcurrentDuplicateRequestSameRider() throws Exception {
        System.out.println("\n==============================================");
        System.out.println("TEST 5: Concurrent Duplicate Request Same Rider");
        System.out.println("==============================================");

        TestContext ctx = createContext();

        Rider rider = new Rider("R40", "User-40", new Location(100, 100));
        ctx.riderRepository.save(rider);

        Driver d1 = new Driver(
                "D40",
                "Driver-40",
                new Cab("C40", VehicleType.SUV, new Location(101, 101), 3)
        );
        Driver d2 = new Driver(
                "D41",
                "Driver-41",
                new Cab("C41", VehicleType.SUV, new Location(102, 102), 3)
        );
        ctx.driverRepository.save(d1);
        ctx.driverRepository.save(d2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        List<String> outcomes = Collections.synchronizedList(new ArrayList<>());

        Runnable task = () -> {
            try {
                readyLatch.countDown();
                startLatch.await();

                PoolBooking booking = ctx.poolBookingService.requestPoolRide(
                        "R40",
                        rider.getCurrentLocation(),
                        new Location(110, 110),
                        VehicleType.SUV,
                        1
                );
                outcomes.add("SUCCESS:booking=" + booking.getId() + ",ride=" + booking.getRideId());
            } catch (Exception e) {
                outcomes.add("FAIL:" + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        executor.submit(task);
        executor.submit(task);

        readyLatch.await();
        System.out.println("Both duplicate requests ready. Starting race...");
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        System.out.println("Outcomes = " + outcomes);

        long successCount = outcomes.stream().filter(s -> s.startsWith("SUCCESS")).count();
        assertCondition(successCount == 1, "Only one request for same rider should succeed");
    }

    private void testNoCompatibleRideCreatesNewPoolRide() {
        System.out.println("\n==============================================");
        System.out.println("TEST 6: No Compatible Ride Creates New PoolRide");
        System.out.println("==============================================");

        TestContext ctx = createContext();

        Rider rider1 = new Rider("R50", "User-50", new Location(10, 10));
        Rider rider2 = new Rider("R51", "User-51", new Location(50, 50));
        ctx.riderRepository.save(rider1);
        ctx.riderRepository.save(rider2);

        Driver d1 = new Driver("D50", "Driver-50", new Cab("C50", VehicleType.SEDAN, new Location(11, 11), 3));
        Driver d2 = new Driver("D51", "Driver-51", new Cab("C51", VehicleType.SEDAN, new Location(51, 51), 3));
        ctx.driverRepository.save(d1);
        ctx.driverRepository.save(d2);

        PoolBooking b1 = ctx.poolBookingService.requestPoolRide(
                "R50", rider1.getCurrentLocation(), new Location(20, 20), VehicleType.SEDAN, 1
        );

        PoolBooking b2 = ctx.poolBookingService.requestPoolRide(
                "R51", rider2.getCurrentLocation(), new Location(70, 70), VehicleType.SEDAN, 1
        );

        printPoolBooking(b1, "Booking-1");
        printPoolBooking(b2, "Booking-2");

        assertCondition(!b1.getRideId().equals(b2.getRideId()), "Should create separate rides for incompatible routes");
    }

    private void submitPoolRequestTask(
            ExecutorService executor,
            TestContext ctx,
            String riderId,
            Location pickup,
            Location destination,
            VehicleType vehicleType,
            CountDownLatch readyLatch,
            CountDownLatch startLatch,
            CountDownLatch doneLatch,
            List<String> outcomes
    ) {
        executor.submit(() -> {
            try {
                readyLatch.countDown();
                startLatch.await();

                PoolBooking booking = ctx.poolBookingService.requestPoolRide(
                        riderId, pickup, destination, vehicleType, 1
                );

                outcomes.add("SUCCESS:rider=" + riderId + ",booking=" + booking.getId() + ",ride=" + booking.getRideId());
            } catch (Exception e) {
                outcomes.add("FAIL:rider=" + riderId + ",reason=" + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        });
    }

    // =========================================================
    // ========================= SETUP ==========================
    // =========================================================

    private TestContext createContext() {
        RiderRepository riderRepository = new InMemoryRiderRepository();
        DriverRepository driverRepository = new InMemoryDriverRepository();
        PoolRideRepository poolRideRepository = new InMemoryPoolRideRepository();
        PoolBookingRepository poolBookingRepository = new InMemoryPoolBookingRepository();

        PoolMatchStrategy poolMatchStrategy = new SimplePoolMatchStrategy();
        PricingStrategy pricingStrategy = new DefaultPricingStrategy();

        PoolBookingService poolBookingService = new PoolBookingService(
                riderRepository,
                driverRepository,
                poolRideRepository,
                poolBookingRepository,
                poolMatchStrategy,
                pricingStrategy
        );

        PoolRideLifecycleService poolRideLifecycleService = new PoolRideLifecycleService(
                poolRideRepository,
                poolBookingRepository,
                driverRepository,
                poolBookingService
        );

        return new TestContext(
                riderRepository,
                driverRepository,
                poolRideRepository,
                poolBookingRepository,
                poolBookingService,
                poolRideLifecycleService
        );
    }

    private void printPoolBooking(PoolBooking booking, String label) {
        System.out.println(label + " => bookingId=" + booking.getId()
                + ", rideId=" + booking.getRideId()
                + ", riderId=" + booking.getRiderId()
                + ", seats=" + booking.getSeatsRequested()
                + ", status=" + booking.getStatus()
                + ", estimatedFare=" + booking.getEstimatedFare());
    }

    private void printPoolRide(PoolRide ride, String label) {
        System.out.println(label + " => rideId=" + ride.getId()
                + ", driverId=" + ride.getDriverId()
                + ", cabId=" + ride.getCabId()
                + ", status=" + ride.getStatus()
                + ", capacity=" + ride.getCapacity()
                + ", availableSeats=" + ride.getAvailableSeats()
                + ", bookingCount=" + ride.getBookingIds().size());
    }

    private void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Assertion failed: " + message);
        }
    }

    // =========================================================
    // ========================= CONTEXT ========================
    // =========================================================

    private static class TestContext {
        private final RiderRepository riderRepository;
        private final DriverRepository driverRepository;
        private final PoolRideRepository poolRideRepository;
        private final PoolBookingRepository poolBookingRepository;
        private final PoolBookingService poolBookingService;
        private final PoolRideLifecycleService poolRideLifecycleService;

        private TestContext(
                RiderRepository riderRepository,
                DriverRepository driverRepository,
                PoolRideRepository poolRideRepository,
                PoolBookingRepository poolBookingRepository,
                PoolBookingService poolBookingService,
                PoolRideLifecycleService poolRideLifecycleService
        ) {
            this.riderRepository = riderRepository;
            this.driverRepository = driverRepository;
            this.poolRideRepository = poolRideRepository;
            this.poolBookingRepository = poolBookingRepository;
            this.poolBookingService = poolBookingService;
            this.poolRideLifecycleService = poolRideLifecycleService;
        }
    }
}