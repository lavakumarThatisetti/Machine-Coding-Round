package com.lavakumar.expenserulenegine.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ExpenseItem(String expenseId,
                          String employeeId,
                          String tripId,
                          ExpenseCategory category,
                          String description,
                          BigDecimal amount,
                          Instant occurredAt) {
}
