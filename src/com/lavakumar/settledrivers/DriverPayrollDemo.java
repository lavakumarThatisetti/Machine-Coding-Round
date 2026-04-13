package com.lavakumar.settledrivers;

import com.lavakumar.settledrivers.dto.DriverSummary;
import com.lavakumar.settledrivers.dto.SettlementReceipt;
import com.lavakumar.settledrivers.repository.DriverRepository;
import com.lavakumar.settledrivers.repository.TripRepository;
import com.lavakumar.settledrivers.service.DriverPayrollService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class DriverPayrollDemo {
    public static void main(String[] args) {
        DriverRepository driverRepository = new DriverRepository();
        TripRepository tripRepository = new TripRepository();
        DriverPayrollService payrollService = new DriverPayrollService(driverRepository, tripRepository);

        runHappyPath(payrollService);
        runLateTripScenario(payrollService);
        runIdempotencyScenario(payrollService);
    }

    private static void runHappyPath(DriverPayrollService payrollService) {
        System.out.println("========== HAPPY PATH ==========");

        payrollService.addDriver("D1", "Alice", new BigDecimal("600.00")); // 600/hour
        payrollService.addDriver("D2", "Bob", new BigDecimal("300.00"));   // 300/hour

        Instant dayStart = Instant.parse("2026-04-13T09:00:00Z");

        payrollService.recordTrip("T1", "D1", dayStart, dayStart.plus(Duration.ofMinutes(30))); // 300
        payrollService.recordTrip("T2", "D1", dayStart.plus(Duration.ofMinutes(45)), dayStart.plus(Duration.ofMinutes(105))); // 600
        payrollService.recordTrip("T3", "D2", dayStart.plus(Duration.ofMinutes(10)), dayStart.plus(Duration.ofMinutes(40))); // 150

        System.out.println("Total cost      : " + payrollService.getTotalCost());   // 1050.00
        System.out.println("Paid cost       : " + payrollService.getPaidCost());    // 0.00
        System.out.println("Unpaid cost     : " + payrollService.getUnpaidCost());  // 1050.00

        SettlementReceipt receipt1 = payrollService.payUpToTime(dayStart.plus(Duration.ofMinutes(30)));
        System.out.println("Settlement #1   : " + receipt1);

        System.out.println("Paid cost       : " + payrollService.getPaidCost());
        System.out.println("Unpaid cost     : " + payrollService.getUnpaidCost());

        SettlementReceipt receipt2 = payrollService.payUpToTime(dayStart.plus(Duration.ofMinutes(60)));
        System.out.println("Settlement #2   : " + receipt2);

        System.out.println("Paid cost       : " + payrollService.getPaidCost());
        System.out.println("Unpaid cost     : " + payrollService.getUnpaidCost());

        System.out.println("Driver D1       : " + payrollService.getDriverSummary("D1"));
        System.out.println("Driver D2       : " + payrollService.getDriverSummary("D2"));
        System.out.println();
    }

    private static void runLateTripScenario(DriverPayrollService payrollService) {
        System.out.println("========== LATE TRIP SCENARIO ==========");

        Instant dayStart = Instant.parse("2026-04-13T09:00:00Z");

        // This trip happened in the past but gets recorded later.
        // Since each trip tracks its own settledUntil, it will still be settled correctly later.
        payrollService.recordTrip(
                "T4",
                "D2",
                dayStart.plus(Duration.ofMinutes(5)),
                dayStart.plus(Duration.ofMinutes(15))
        ); // 10 min at 300/hour = 50

        System.out.println("After late trip total cost : " + payrollService.getTotalCost());
        System.out.println("After late trip unpaid     : " + payrollService.getUnpaidCost());

        SettlementReceipt receipt = payrollService.payUpToTime(dayStart.plus(Duration.ofMinutes(120)));
        System.out.println("Settlement #3              : " + receipt);

        System.out.println("Paid cost                  : " + payrollService.getPaidCost());
        System.out.println("Unpaid cost                : " + payrollService.getUnpaidCost());
        System.out.println();
    }

    private static void runIdempotencyScenario(DriverPayrollService payrollService) {
        System.out.println("========== IDEMPOTENCY ==========");

        Instant sameTime = Instant.parse("2026-04-13T11:00:00Z");

        SettlementReceipt receipt1 = payrollService.payUpToTime(sameTime);
        SettlementReceipt receipt2 = payrollService.payUpToTime(sameTime);

        System.out.println("Repeated settlement #1 : " + receipt1);
        System.out.println("Repeated settlement #2 : " + receipt2);
        System.out.println("Final paid             : " + payrollService.getPaidCost());
        System.out.println("Final unpaid           : " + payrollService.getUnpaidCost());
        System.out.println();

        System.out.println("========== ALL DRIVER SUMMARIES ==========");
        for (DriverSummary summary : payrollService.getAllDriverSummaries()) {
            System.out.println(summary);
        }
    }
}
