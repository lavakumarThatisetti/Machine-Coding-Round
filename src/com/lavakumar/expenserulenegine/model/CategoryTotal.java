package com.lavakumar.expenserulenegine.model;

import java.math.BigDecimal;

public record CategoryTotal(ExpenseCategory category,
                            BigDecimal totalApprovedAmount) {
}
