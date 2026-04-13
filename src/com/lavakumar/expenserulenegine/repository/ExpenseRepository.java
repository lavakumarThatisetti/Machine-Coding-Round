package com.lavakumar.expenserulenegine.repository;

import com.lavakumar.expenserulenegine.model.*;
import com.lavakumar.expenserulenegine.service.Money;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.*;

public class ExpenseRepository {
    private final ConcurrentHashMap<String, SubmittedExpense> expensesById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SubmittedExpense>> expensesByEmployee = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SubmittedExpense>> expensesByTrip = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, BigDecimal> approvedTotalByEmployee = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BigDecimal> approvedTotalByTrip = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ExpenseCategory, BigDecimal> approvedTotalByCategory = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<TripCategoryKey, BigDecimal> approvedTotalByTripCategory = new ConcurrentHashMap<>();

    public boolean exists(String expenseId) {
        return expensesById.containsKey(expenseId);
    }

    public void save(SubmittedExpense submittedExpense) {
        SubmittedExpense existing = expensesById.putIfAbsent(
                submittedExpense.expense().expenseId(),
                submittedExpense
        );

        if (existing != null) {
            throw new IllegalArgumentException("Expense already exists: " + submittedExpense.expense().expenseId());
        }

        ExpenseItem expense = submittedExpense.expense();

        expensesByEmployee
                .computeIfAbsent(expense.employeeId(), ignored -> new CopyOnWriteArrayList<>())
                .add(submittedExpense);

        if (expense.tripId() != null) {
            expensesByTrip
                    .computeIfAbsent(expense.tripId(), ignored -> new CopyOnWriteArrayList<>())
                    .add(submittedExpense);
        }

        if (submittedExpense.status() == ExpenseStatus.APPROVED) {
            approvedTotalByEmployee.merge(expense.employeeId(), expense.amount(), Money::add);

            if (expense.tripId() != null) {
                approvedTotalByTrip.merge(expense.tripId(), expense.amount(), Money::add);
                approvedTotalByTripCategory.merge(
                        new TripCategoryKey(expense.tripId(), expense.category()),
                        expense.amount(),
                        Money::add
                );
            }

            approvedTotalByCategory.merge(expense.category(), expense.amount(), Money::add);
        }
    }

    public List<SubmittedExpense> findAll() {
        return List.copyOf(expensesById.values());
    }

    public List<SubmittedExpense> findByEmployeeId(String employeeId) {
        return List.copyOf(expensesByEmployee.getOrDefault(employeeId, new CopyOnWriteArrayList<>()));
    }

    public List<SubmittedExpense> findByTripId(String tripId) {
        return List.copyOf(expensesByTrip.getOrDefault(tripId, new CopyOnWriteArrayList<>()));
    }

    public BigDecimal getApprovedTotalByEmployee(String employeeId) {
        return approvedTotalByEmployee.getOrDefault(employeeId, Money.zero());
    }

    public BigDecimal getApprovedTotalByTrip(String tripId) {
        return approvedTotalByTrip.getOrDefault(tripId, Money.zero());
    }

    public BigDecimal getApprovedTotalByCategory(ExpenseCategory category) {
        return approvedTotalByCategory.getOrDefault(category, Money.zero());
    }

    public BigDecimal getApprovedTotalByTripAndCategory(String tripId, ExpenseCategory category) {
        return approvedTotalByTripCategory.getOrDefault(new TripCategoryKey(tripId, category), Money.zero());
    }
}
