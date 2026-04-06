import entity.*;
import repository.*;
import service.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        Main demo = new Main();

        demo.testHappyPath_RequestAcceptStartComplete();
        demo.testCancelBeforeAccept();
        demo.testCancelAfterAcceptBeforeStart();
        demo.testConcurrentDriverAcceptanceRace();
        demo.testConcurrentDuplicateRideRequestForSameRider();
        demo.testInvalidLifecycleTransitions();
    }

    // =========================================================
    // ===================== TEST CASES =========================
    // =========================================================

    private void testHappyPath_RequestAcceptStartComplete() {
        System.out.println("\n==============================");
        System.out.println("TEST 1: Happy Path");
        System.out.println("==============================");

        TestContext ctx = createContext();

        Rider rider = new Rider("R1", "Lava", new Location(10, 10));
        ctx.riderRepository.save(rider);

        Driver d1 = new Driver("D1", "Driver-1", new Cab("C1", VehicleType.SEDAN, new Location(11, 11)));
        Driver d2 = new Driver("D2", "Driver-2", new Cab("C2", VehicleType.SEDAN, new Location(14, 14)));
        ctx.driverRepository.save(d1);
        ctx.driverRepository.save(d2);

        RideBooking booking = ctx.rideRequestService.requestRide("R1", new Location(20, 20), VehicleType.SEDAN);
        printRide(booking, "After request");

        boolean accepted = ctx.driverAcceptanceService.acceptRide("D1", booking.getId());
        System.out.println("D1 accepted? " + accepted);
        printRide(ctx.bookingRepository.findById(booking.getId()).orElseThrow(), "After accept");

        ctx.rideLifecycleService.startRide(booking.getId());
        printRide(ctx.bookingRepository.findById(booking.getId()).orElseThrow(), "After start");

        ctx.rideLifecycleService.completeRide(booking.getId());
        printRide(ctx.bookingRepository.findById(booking.getId()).orElseThrow(), "After complete");

        System.out.println("Driver status after complete: " + d1.getStatus());
        System.out.println("Cab status after complete: " + d1.getCab().getStatus());
    }

    private void testCancelBeforeAccept() {
        System.out.println("\n==============================");
        System.out.println("TEST 2: Cancel Before Accept");
        System.out.println("==============================");

        TestContext ctx = createContext();

        Rider rider = new Rider("R2", "Rider-2", new Location(10, 10));
        ctx.riderRepository.save(rider);

        Driver d1 = new Driver("D10", "Driver-10", new Cab("C10", VehicleType.HATCHBACK, new Location(12, 12)));
        Driver d2 = new Driver("D11", "Driver-11", new Cab("C11", VehicleType.HATCHBACK, new Location(13, 13)));
        ctx.driverRepository.save(d1);
        ctx.driverRepository.save(d2);

        RideBooking booking = ctx.rideRequestService.requestRide("R2", new Location(18, 18), VehicleType.HATCHBACK);
        printRide(booking, "After request");

        ctx.rideLifecycleService.cancelRide(booking.getId());
        RideBooking updated = ctx.bookingRepository.findById(booking.getId()).orElseThrow();
        printRide(updated, "After cancel");

        System.out.println("D10 status: " + d1.getStatus());
        System.out.println("D11 status: " + d2.getStatus());
    }

    private void testCancelAfterAcceptBeforeStart() {
        System.out.println("\n==========================================");
        System.out.println("TEST 3: Cancel After Accept Before Start");
        System.out.println("==========================================");

        TestContext ctx = createContext();

        Rider rider = new Rider("R3", "Rider-3", new Location(5, 5));
        ctx.riderRepository.save(rider);

        Driver d1 = new Driver("D20", "Driver-20", new Cab("C20", VehicleType.SUV, new Location(6, 6)));
        Driver d2 = new Driver("D21", "Driver-21", new Cab("C21", VehicleType.SUV, new Location(8, 8)));
        ctx.driverRepository.save(d1);
        ctx.driverRepository.save(d2);

        RideBooking booking = ctx.rideRequestService.requestRide("R3", new Location(25, 25), VehicleType.SUV);
        boolean accepted = ctx.driverAcceptanceService.acceptRide("D20", booking.getId());

        System.out.println("D20 accepted? " + accepted);
        printRide(ctx.bookingRepository.findById(booking.getId()).orElseThrow(), "After accept");

        ctx.rideLifecycleService.cancelRide(booking.getId());
        RideBooking updated = ctx.bookingRepository.findById(booking.getId()).orElseThrow();
        printRide(updated, "After cancel");

        System.out.println("Driver status after cancel: " + d1.getStatus());
        System.out.println("Cab status after cancel: " + d1.getCab().getStatus());
    }

    private void testConcurrentDriverAcceptanceRace() throws Exception {
        System.out.println("\n==========================================");
        System.out.println("TEST 4: Concurrent Driver Acceptance Race");
        System.out.println("==========================================");

        TestContext ctx = createContext();

        Rider rider = new Rider("R4", "Rider-4", new Location(10, 10));
        ctx.riderRepository.save(rider);

        Driver d1 = new Driver("D31", "Driver-31", new Cab("C31", VehicleType.SEDAN, new Location(11, 11)));
        Driver d2 = new Driver("D32", "Driver-32", new Cab("C32", VehicleType.SEDAN, new Location(11.2, 11.2)));
        Driver d3 = new Driver("D33", "Driver-33", new Cab("C33", VehicleType.SEDAN, new Location(11.5, 11.5)));

        ctx.driverRepository.save(d1);
        ctx.driverRepository.save(d2);
        ctx.driverRepository.save(d3);

        RideBooking booking = ctx.rideRequestService.requestRide("R4", new Location(30, 30), VehicleType.SEDAN);
        printRide(booking, "After request");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch readyLatch = new CountDownLatch(3);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(3);

        Map<String, Boolean> results = new ConcurrentHashMap<>();

        submitAcceptanceTask(executor, ctx, "D31", booking.getId(), readyLatch, startLatch, doneLatch, results);
        submitAcceptanceTask(executor, ctx, "D32", booking.getId(), readyLatch, startLatch, doneLatch, results);
        submitAcceptanceTask(executor, ctx, "D33", booking.getId(), readyLatch, startLatch, doneLatch, results);

        readyLatch.await();
        System.out.println("All driver threads ready. Triggering race...");
        startLatch.countDown();
        doneLatch.await();

        executor.shutdown();

        RideBooking updated = ctx.bookingRepository.findById(booking.getId()).orElseThrow();
        printRide(updated, "After concurrent acceptance");

        long successCount = results.values().stream().filter(Boolean::booleanValue).count();
        System.out.println("Acceptance results: " + results);
        System.out.println("Success count should be 1, actual = " + successCount);

        assertCondition(successCount == 1, "Exactly one driver should win the acceptance race");
    }

    private void testConcurrentDuplicateRideRequestForSameRider() throws Exception {
        System.out.println("\n====================================================");
        System.out.println("TEST 5: Concurrent Duplicate Ride Request Same Rider");
        System.out.println("====================================================");

        TestContext ctx = createContext();

        Rider rider = new Rider("R5", "Rider-5", new Location(50, 50));
        ctx.riderRepository.save(rider);

        Driver d1 = new Driver("D41", "Driver-41", new Cab("C41", VehicleType.HATCHBACK, new Location(51, 51)));
        Driver d2 = new Driver("D42", "Driver-42", new Cab("C42", VehicleType.HATCHBACK, new Location(52, 52)));
        ctx.driverRepository.save(d1);
        ctx.driverRepository.save(d2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        List<String> outcomes = Collections.synchronizedList(new ArrayList<>());

        Runnable requestTask = () -> {
            try {
                readyLatch.countDown();
                startLatch.await();
                RideBooking booking = ctx.rideRequestService.requestRide("R5", new Location(60, 60), VehicleType.HATCHBACK);
                outcomes.add("SUCCESS:" + booking.getId());
            } catch (Exception e) {
                outcomes.add("FAIL:" + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        executor.submit(requestTask);
        executor.submit(requestTask);

        readyLatch.await();
        System.out.println("Both rider request threads ready. Triggering race...");
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        System.out.println("Outcomes: " + outcomes);

        long successCount = outcomes.stream().filter(s -> s.startsWith("SUCCESS")).count();
        assertCondition(successCount == 1, "Exactly one request should succeed for same rider");
    }

    private void testInvalidLifecycleTransitions() {
        System.out.println("\n==================================");
        System.out.println("TEST 6: Invalid Lifecycle Checks");
        System.out.println("==================================");

        TestContext ctx = createContext();

        Rider rider = new Rider("R6", "Rider-6", new Location(1, 1));
        ctx.riderRepository.save(rider);

        Driver d1 = new Driver("D51", "Driver-51", new Cab("C51", VehicleType.SUV, new Location(2, 2)));
        ctx.driverRepository.save(d1);

        RideBooking booking = ctx.rideRequestService.requestRide("R6", new Location(9, 9), VehicleType.SUV);

        try {
            ctx.rideLifecycleService.startRide(booking.getId());
            throw new RuntimeException("Expected startRide to fail before accept");
        } catch (IllegalStateException ex) {
            System.out.println("Expected failure on start before accept: " + ex.getMessage());
        }

        boolean accepted = ctx.driverAcceptanceService.acceptRide("D51", booking.getId());
        System.out.println("Accepted? " + accepted);

        ctx.rideLifecycleService.startRide(booking.getId());

        try {
            ctx.rideLifecycleService.startRide(booking.getId());
            throw new RuntimeException("Expected second startRide to fail");
        } catch (IllegalStateException ex) {
            System.out.println("Expected failure on duplicate start: " + ex.getMessage());
        }

        ctx.rideLifecycleService.completeRide(booking.getId());

        try {
            ctx.rideLifecycleService.cancelRide(booking.getId());
            throw new RuntimeException("Expected cancel after complete to fail");
        } catch (IllegalStateException ex) {
            System.out.println("Expected failure on cancel after complete: " + ex.getMessage());
        }
    }

    private void submitAcceptanceTask(
            ExecutorService executor,
            TestContext ctx,
            String driverId,
            String rideId,
            CountDownLatch readyLatch,
            CountDownLatch startLatch,
            CountDownLatch doneLatch,
            Map<String, Boolean> results
    ) {
        executor.submit(() -> {
            try {
                readyLatch.countDown();
                startLatch.await();
                boolean accepted = ctx.driverAcceptanceService.acceptRide(driverId, rideId);
                results.put(driverId, accepted);
            } catch (Exception e) {
                e.printStackTrace();
                results.put(driverId, false);
            } finally {
                doneLatch.countDown();
            }
        });
    }

    private void printRide(RideBooking booking, String label) {
        System.out.println(label + " => rideId=" + booking.getId()
                + ", riderId=" + booking.getRiderId()
                + ", driverId=" + booking.getDriverId()
                + ", cabId=" + booking.getCabId()
                + ", status=" + booking.getStatus()
                + ", estimatedFare=" + booking.getEstimatedFare()
                + ", finalFare=" + booking.getFinalFare());
    }

    private void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Assertion failed: " + message);
        }
    }

    private TestContext createContext() {
        RiderRepository riderRepository = new InMemoryRiderRepository();
        DriverRepository driverRepository = new InMemoryDriverRepository();
        RideBookingRepository bookingRepository = new InMemoryRideBookingRepository();

        PricingStrategy pricingStrategy = new DefaultPricingStrategy();
        MatchingStrategy matchingStrategy = new NearestDriverMatchingStrategy(5);
        DriverNotificationService notificationService = new LoggingDriverNotificationService();
        DriverMatchingEngine driverMatchingEngine = new DriverMatchingEngine(driverRepository, matchingStrategy);

        RideRequestService rideRequestService = new RideRequestService(
                riderRepository,
                bookingRepository,
                pricingStrategy,
                notificationService,
                driverMatchingEngine
        );

        DriverAcceptanceService driverAcceptanceService = new DriverAcceptanceService(
                driverRepository,
                bookingRepository
        );

        RideLifecycleService rideLifecycleService = new RideLifecycleService(
                bookingRepository,
                driverRepository,
                pricingStrategy,
                rideRequestService
        );

        return new TestContext(
                riderRepository,
                driverRepository,
                bookingRepository,
                rideRequestService,
                driverAcceptanceService,
                rideLifecycleService
        );
    }

    // =========================================================
    // ======================= CONTEXT ==========================
    // =========================================================

    private static class TestContext {
        private final RiderRepository riderRepository;
        private final DriverRepository driverRepository;
        private final RideBookingRepository bookingRepository;
        private final RideRequestService rideRequestService;
        private final DriverAcceptanceService driverAcceptanceService;
        private final RideLifecycleService rideLifecycleService;

        private TestContext(
                RiderRepository riderRepository,
                DriverRepository driverRepository,
                RideBookingRepository bookingRepository,
                RideRequestService rideRequestService,
                DriverAcceptanceService driverAcceptanceService,
                RideLifecycleService rideLifecycleService
        ) {
            this.riderRepository = riderRepository;
            this.driverRepository = driverRepository;
            this.bookingRepository = bookingRepository;
            this.rideRequestService = rideRequestService;
            this.driverAcceptanceService = driverAcceptanceService;
            this.rideLifecycleService = rideLifecycleService;
        }
    }
}