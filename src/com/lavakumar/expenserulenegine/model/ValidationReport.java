package com.lavakumar.expenserulenegine.model;

import java.util.List;

public record ValidationReport(
        boolean approved,
        List<RuleViolation> violations
) {}
