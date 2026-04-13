package com.lavakumar.expenserulenegine.model;

public record SubmissionResult(String expenseId,
                               ExpenseStatus status,
                               ValidationReport validationReport) {
}
