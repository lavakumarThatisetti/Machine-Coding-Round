package com.lavakumar.expenserulenegine.model;

import java.math.BigDecimal;

public record EmployeeCostSummary(
        String employeeId,
        BigDecimal totalApprovedCost,
        long approvedExpenseCount,
        long rejectedExpenseCount
) {
}
