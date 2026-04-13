package com.lavakumar.expenserulenegine.service;

import com.lavakumar.expenserulenegine.model.ExpenseCategory;
import com.lavakumar.expenserulenegine.repository.ExpenseRepository;

import java.math.BigDecimal;

public class ValidationContext {
    private final ExpenseRepository expenseRepository;

    ValidationContext(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public BigDecimal currentApprovedTotalForEmployee(String employeeId) {
        return expenseRepository.getApprovedTotalByEmployee(employeeId);
    }

    public BigDecimal currentApprovedTotalForTrip(String tripId) {
        if (tripId == null || tripId.isBlank()) {
            return Money.zero();
        }
        return expenseRepository.getApprovedTotalByTrip(tripId);
    }

    public BigDecimal currentApprovedTotalForCategory(ExpenseCategory category) {
        return expenseRepository.getApprovedTotalByCategory(category);
    }

    public BigDecimal currentApprovedTotalForTripAndCategory(String tripId, ExpenseCategory category) {
        if (tripId == null || tripId.isBlank()) {
            return Money.zero();
        }
        return expenseRepository.getApprovedTotalByTripAndCategory(tripId, category);
    }
}
