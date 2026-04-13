package com.lavakumar.expenserulenegine.service;

import com.lavakumar.expenserulenegine.model.*;
import com.lavakumar.expenserulenegine.repository.ExpenseRepository;
import com.lavakumar.expenserulenegine.rule.ExpenseRule;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CostExplorerService {
    private final ExpenseRepository expenseRepository;
    private final CopyOnWriteArrayList<ExpenseRule> rules = new CopyOnWriteArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public CostExplorerService(ExpenseRepository expenseRepository) {
        this.expenseRepository = Objects.requireNonNull(expenseRepository);
    }

    public void addRule(ExpenseRule rule) {
        Objects.requireNonNull(rule, "rule must not be null");

        lock.writeLock().lock();
        try {
            boolean duplicate = rules.stream().anyMatch(existing -> existing.ruleId().equals(rule.ruleId()));
            if (duplicate) {
                throw new IllegalArgumentException("Duplicate ruleId: " + rule.ruleId());
            }
            rules.add(rule);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public ValidationReport validateExpense(ExpenseItem rawExpense) {
        ExpenseItem expense = sanitizeExpense(rawExpense);

        lock.readLock().lock();
        try {
            ValidationContext context = new ValidationContext(expenseRepository);

            List<RuleViolation> violations = new ArrayList<>();
            for (ExpenseRule rule : rules) {
                Optional<RuleViolation> violation = rule.validate(expense, context);
                violation.ifPresent(violations::add);
            }

            return new ValidationReport(violations.isEmpty(), List.copyOf(violations));
        } finally {
            lock.readLock().unlock();
        }
    }

    public SubmissionResult submitExpense(ExpenseItem rawExpense) {
        ExpenseItem expense = sanitizeExpense(rawExpense);

        lock.writeLock().lock();
        try {
            if (expenseRepository.exists(expense.expenseId())) {
                throw new IllegalArgumentException("Expense already exists: " + expense.expenseId());
            }

            ValidationContext context = new ValidationContext(expenseRepository);

            List<RuleViolation> violations = new ArrayList<>();
            for (ExpenseRule rule : rules) {
                Optional<RuleViolation> violation = rule.validate(expense, context);
                violation.ifPresent(violations::add);
            }

            ValidationReport report = new ValidationReport(violations.isEmpty(), List.copyOf(violations));
            ExpenseStatus status = report.approved() ? ExpenseStatus.APPROVED : ExpenseStatus.REJECTED;

            SubmittedExpense submittedExpense = new SubmittedExpense(
                    expense,
                    status,
                    report,
                    Instant.now()
            );

            expenseRepository.save(submittedExpense);

            return new SubmissionResult(expense.expenseId(), status, report);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public BigDecimal getApprovedTotalByEmployee(String employeeId) {
        validateEmployeeId(employeeId);

        lock.readLock().lock();
        try {
            return Money.toDisplay(expenseRepository.getApprovedTotalByEmployee(employeeId));
        } finally {
            lock.readLock().unlock();
        }
    }

    public BigDecimal getApprovedTotalByTrip(String tripId) {
        validateTripId(tripId);

        lock.readLock().lock();
        try {
            return Money.toDisplay(expenseRepository.getApprovedTotalByTrip(tripId));
        } finally {
            lock.readLock().unlock();
        }
    }

    public BigDecimal getApprovedTotalByCategory(ExpenseCategory category) {
        Objects.requireNonNull(category, "category must not be null");

        lock.readLock().lock();
        try {
            return Money.toDisplay(expenseRepository.getApprovedTotalByCategory(category));
        } finally {
            lock.readLock().unlock();
        }
    }

    public BigDecimal getApprovedTotalByTripAndCategory(String tripId, ExpenseCategory category) {
        validateTripId(tripId);
        Objects.requireNonNull(category, "category must not be null");

        lock.readLock().lock();
        try {
            return Money.toDisplay(expenseRepository.getApprovedTotalByTripAndCategory(tripId, category));
        } finally {
            lock.readLock().unlock();
        }
    }

    public TripCostSummary getTripSummary(String tripId) {
        validateTripId(tripId);

        lock.readLock().lock();
        try {
            List<SubmittedExpense> tripExpenses = expenseRepository.findByTripId(tripId);

            long approvedCount = tripExpenses.stream()
                    .filter(expense -> expense.status() == ExpenseStatus.APPROVED)
                    .count();

            long rejectedCount = tripExpenses.stream()
                    .filter(expense -> expense.status() == ExpenseStatus.REJECTED)
                    .count();

            List<CategoryTotal> categoryTotals = new ArrayList<>();
            for (ExpenseCategory category : ExpenseCategory.values()) {
                BigDecimal total = expenseRepository.getApprovedTotalByTripAndCategory(tripId, category);
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    categoryTotals.add(new CategoryTotal(category, Money.toDisplay(total)));
                }
            }

            categoryTotals.sort(Comparator .comparing(categoryTotal -> categoryTotal.category().name()));

            return new TripCostSummary(
                    tripId,
                    Money.toDisplay(expenseRepository.getApprovedTotalByTrip(tripId)),
                    approvedCount,
                    rejectedCount,
                    List.copyOf(categoryTotals)
            );
        } finally {
            lock.readLock().unlock();
        }
    }

    public EmployeeCostSummary getEmployeeSummary(String employeeId) {
        validateEmployeeId(employeeId);

        lock.readLock().lock();
        try {
            List<SubmittedExpense> employeeExpenses = expenseRepository.findByEmployeeId(employeeId);

            long approvedCount = employeeExpenses.stream()
                    .filter(expense -> expense.status() == ExpenseStatus.APPROVED)
                    .count();

            long rejectedCount = employeeExpenses.stream()
                    .filter(expense -> expense.status() == ExpenseStatus.REJECTED)
                    .count();

            return new EmployeeCostSummary(
                    employeeId,
                    Money.toDisplay(expenseRepository.getApprovedTotalByEmployee(employeeId)),
                    approvedCount,
                    rejectedCount
            );
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<SubmittedExpense> getAllSubmittedExpenses() {
        lock.readLock().lock();
        try {
            List<SubmittedExpense> expenses = new ArrayList<>(expenseRepository.findAll());
            expenses.sort(Comparator.comparing(expense -> expense.expense().expenseId()));
            return expenses;
        } finally {
            lock.readLock().unlock();
        }
    }

    private ExpenseItem sanitizeExpense(ExpenseItem rawExpense) {
        Objects.requireNonNull(rawExpense, "expense must not be null");

        validateExpenseId(rawExpense.expenseId());
        validateEmployeeId(rawExpense.employeeId());
        Objects.requireNonNull(rawExpense.category(), "category must not be null");
        Objects.requireNonNull(rawExpense.occurredAt(), "occurredAt must not be null");

        if (rawExpense.amount() == null || rawExpense.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }

        String tripId = normalizeNullable(rawExpense.tripId());
        String description = normalizeNullable(rawExpense.description());

        return new ExpenseItem(
                rawExpense.expenseId().trim(),
                rawExpense.employeeId().trim(),
                tripId,
                rawExpense.category(),
                description,
                Money.normalize(rawExpense.amount()),
                rawExpense.occurredAt()
        );
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validateExpenseId(String expenseId) {
        if (expenseId == null || expenseId.isBlank()) {
            throw new IllegalArgumentException("expenseId must not be blank");
        }
    }

    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("employeeId must not be blank");
        }
    }

    private void validateTripId(String tripId) {
        if (tripId == null || tripId.isBlank()) {
            throw new IllegalArgumentException("tripId must not be blank");
        }
    }
}
