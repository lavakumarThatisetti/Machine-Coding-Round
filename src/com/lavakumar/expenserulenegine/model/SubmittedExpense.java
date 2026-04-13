package com.lavakumar.expenserulenegine.model;

import java.time.Instant;

public record SubmittedExpense(ExpenseItem expense,
                               ExpenseStatus status,
                               ValidationReport validationReport,
                               Instant processedAt) {
}
