package com.lavakumar.expenserulenegine.model;

import java.math.BigDecimal;
import java.util.List;

public record TripCostSummary(String tripId,
                              BigDecimal totalApprovedCost,
                              long approvedExpenseCount,
                              long rejectedExpenseCount,
                              List<CategoryTotal> approvedCategoryTotals) {
}
