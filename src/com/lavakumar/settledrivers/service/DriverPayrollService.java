package com.lavakumar.settledrivers.service;

import com.lavakumar.settledrivers.dto.DriverSummary;
import com.lavakumar.settledrivers.dto.SettlementReceipt;
import com.lavakumar.settledrivers.dto.TripSettlement;
import com.lavakumar.settledrivers.dto.TripView;
import com.lavakumar.settledrivers.model.DeliveryTrip;
import com.lavakumar.settledrivers.model.Driver;
import com.lavakumar.settledrivers.repository.DriverRepository;
import com.lavakumar.settledrivers.repository.TripRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DriverPayrollService {
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private BigDecimal totalAccruedCost = Money.zero();
    private BigDecimal totalPaidCost = Money.zero();

    private final ConcurrentHashMap<String, BigDecimal> accruedAmountByDriver = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BigDecimal> paidToDriver = new ConcurrentHashMap<>();

    public DriverPayrollService(DriverRepository driverRepository, TripRepository tripRepository) {
        this.driverRepository = Objects.requireNonNull(driverRepository);
        this.tripRepository = Objects.requireNonNull(tripRepository);
    }

    public void addDriver(String driverId, String driverName, BigDecimal hourlyRate) {
        validateDriverId(driverId);
        validateDriverName(driverName);
        validateHourlyRate(hourlyRate);

        Driver driver = new Driver(driverId, driverName, Money.normalize(hourlyRate));
        lock.writeLock().lock();
        try {
            boolean inserted = driverRepository.save(driver);
            if (!inserted) {
                throw new IllegalArgumentException("Driver already exists: " + driverId);
            }

            accruedAmountByDriver.putIfAbsent(driverId, Money.zero());
            paidToDriver.putIfAbsent(driverId, Money.zero());
        } finally {
            lock.writeLock().unlock();
        }
    }

    // This means: “a completed trip happened, store it in the system.”
    /*
    Example:
            driver D1 rate = 600/hour
            trip start = 9:00
            trip end = 10:00

            When you call recordTrip("T1", "D1", 9:00, 10:00), the system now knows:

            driver worked 1 hour
            company owes driver 600 for this trip

            At this point:

            accrued = 600
            paid = 0

            Because money is owed, but not yet disbursed.


            What is accrued?

                accrued means:

                money the driver has earned so far based on completed work

                It is the company’s liability.

                So if 3 completed trips together are worth 1050, then:

                total accrued = 1050

                even if payroll has not yet been run.
     */
    public void recordTrip(String tripId, String driverId, Instant startTime, Instant endTime) {
        validateTripId(tripId);
        validateDriverId(driverId);
        validateTripTimes(startTime, endTime);

        lock.writeLock().lock();
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown driver: " + driverId));

            if (tripRepository.exists(tripId)) {
                throw new IllegalArgumentException("Trip already exists: " + tripId);
            }

            BigDecimal totalTripCost = PayrollCalculator.calculateCost(driver.getHourlyRate(), startTime, endTime);

            DeliveryTrip trip = new DeliveryTrip(
                    tripId,
                    driverId,
                    startTime,
                    endTime,
                    totalTripCost
            );

            tripRepository.save(trip);

            totalAccruedCost = Money.add(totalAccruedCost, totalTripCost);
            accruedAmountByDriver.merge(driverId, totalTripCost, Money::add);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /*

    What is paid?

            paid means:

            money that has actually been settled / disbursed to drivers

            So if payroll already transferred 450 out of that 1050, then:

            accrued = 1050
            paid = 450
            unpaid = 600
     What is settledUntil?

    settledUntil is per trip.

            this trip has already been paid up to this timestamp

    Example:

        driver rate = 600/hour
        trip = 9:00 to 10:00
        full trip worth = 600

        Initially, after recordTrip:

        settledUntil = 9:00
        nothing is paid yet

        Now call:

        payUpToTime(9:30)

        That means: pay everything earned up to 9:30.

        So for this trip:

        paid portion = 9:00 to 9:30 = 300
        unpaid portion = 9:30 to 10:00 = 300

        now settledUntil = 9:30

        Later call:

        payUpToTime(11:00)

        Now remaining part gets paid:

        extra paid = 9:30 to 10:00 = 300
        now settledUntil = 10:00
        trip fully settled

        So settledUntil helps us avoid paying the same trip portion twice.

     */

    public SettlementReceipt payUpToTime(Instant upToTime) {
        Objects.requireNonNull(upToTime, "upToTime must not be null");

        lock.writeLock().lock();
        try {
            BigDecimal paidThisRun = Money.zero();
            List<TripSettlement> tripSettlements = new ArrayList<>();

            for (DeliveryTrip trip : tripRepository.findAll()) {
                if (!trip.hasUnpaidWork()) {
                    continue;
                }

                Instant from = trip.getSettledUntil();
                Instant payableUntil = min(trip.getEndTime(), upToTime);

                if (!payableUntil.isAfter(from)) {
                    continue;
                }

                Driver driver = driverRepository.findById(trip.getDriverId())
                        .orElseThrow(() -> new IllegalStateException("Driver missing for trip: " + trip.getTripId()));

                BigDecimal delta = PayrollCalculator.calculateCost(driver.getHourlyRate(), from, payableUntil);
                if (Money.isZero(delta)) {
                    trip.settleUntil(payableUntil);
                    continue;
                }

                trip.settleUntil(payableUntil);

                totalPaidCost = Money.add(totalPaidCost, delta);
                paidToDriver.merge(driver.getDriverId(), delta, Money::add);
                paidThisRun = Money.add(paidThisRun, delta);

                tripSettlements.add(new TripSettlement(
                        trip.getTripId(),
                        driver.getDriverId(),
                        from,
                        payableUntil,
                        Money.toDisplay(delta)
                ));
            }

            tripSettlements.sort(Comparator.comparing(TripSettlement::tripId));

            return new SettlementReceipt(
                    upToTime,
                    Money.toDisplay(paidThisRun),
                    Money.toDisplay(totalPaidCost),
                    Money.toDisplay(Money.subtract(totalAccruedCost, totalPaidCost)),
                    tripSettlements
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    public BigDecimal getTotalCost() {
        lock.readLock().lock();
        try {
            return Money.toDisplay(totalAccruedCost);
        } finally {
            lock.readLock().unlock();
        }
    }

    public BigDecimal getPaidCost() {
        lock.readLock().lock();
        try {
            return Money.toDisplay(totalPaidCost);
        } finally {
            lock.readLock().unlock();
        }
    }

    public BigDecimal getUnpaidCost() {
        lock.readLock().lock();
        try {
            return Money.toDisplay(Money.subtract(totalAccruedCost, totalPaidCost));
        } finally {
            lock.readLock().unlock();
        }
    }

    public DriverSummary getDriverSummary(String driverId) {
        validateDriverId(driverId);

        lock.readLock().lock();
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown driver: " + driverId));

            BigDecimal accrued = accruedAmountByDriver.getOrDefault(driverId, Money.zero());
            BigDecimal paid = paidToDriver.getOrDefault(driverId, Money.zero());
            BigDecimal unpaid = Money.subtract(accrued, paid);

            List<TripView> trips = new ArrayList<>();
            for (DeliveryTrip trip : tripRepository.findByDriverId(driverId)) {
                trips.add(new TripView(
                        trip.getTripId(),
                        trip.getStartTime(),
                        trip.getEndTime(),
                        Money.toDisplay(trip.getTotalCost()),
                        Money.toDisplay(calculatePaidAmountForTrip(driver.getHourlyRate(), trip)),
                        Money.toDisplay(calculateUnpaidAmountForTrip(driver.getHourlyRate(), trip))
                ));
            }

            trips.sort(Comparator.comparing(TripView::tripId));

            return new DriverSummary(
                    driver.getDriverId(),
                    driver.getDriverName(),
                    Money.toDisplay(driver.getHourlyRate()),
                    Money.toDisplay(accrued),
                    Money.toDisplay(paid),
                    Money.toDisplay(unpaid),
                    trips
            );
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<DriverSummary> getAllDriverSummaries() {
        lock.readLock().lock();
        try {
            List<DriverSummary> result = new ArrayList<>();
            for (Driver driver : driverRepository.findAll()) {
                result.add(getDriverSummary(driver.getDriverId()));
            }
            result.sort(Comparator.comparing(DriverSummary::driverId));
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    private BigDecimal calculatePaidAmountForTrip(BigDecimal hourlyRate, DeliveryTrip trip) {
        return PayrollCalculator.calculateCost(hourlyRate, trip.getStartTime(), trip.getSettledUntil());
    }

    private BigDecimal calculateUnpaidAmountForTrip(BigDecimal hourlyRate, DeliveryTrip trip) {
        BigDecimal paid = calculatePaidAmountForTrip(hourlyRate, trip);
        return Money.subtract(trip.getTotalCost(), paid);
    }

    private Instant min(Instant a, Instant b) {
        return a.isBefore(b) ? a : b;
    }

    private void validateDriverId(String driverId) {
        if (driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("driverId must not be blank");
        }
    }

    private void validateDriverName(String driverName) {
        if (driverName == null || driverName.isBlank()) {
            throw new IllegalArgumentException("driverName must not be blank");
        }
    }

    private void validateHourlyRate(BigDecimal hourlyRate) {
        if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("hourlyRate must be > 0");
        }
    }

    private void validateTripId(String tripId) {
        if (tripId == null || tripId.isBlank()) {
            throw new IllegalArgumentException("tripId must not be blank");
        }
    }

    private void validateTripTimes(Instant startTime, Instant endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Trip time must not be null");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Trip endTime must be after startTime");
        }
    }
}
