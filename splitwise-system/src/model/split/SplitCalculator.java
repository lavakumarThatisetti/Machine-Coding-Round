package model.split;

import exception.ValidationException;
import model.Share;
import model.SplitInput;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface SplitCalculator {
    List<Share> calculate(BigDecimal totalAmount, List<SplitInput> inputs);

    default void validateCommon(BigDecimal totalAmount, List<SplitInput> inputs) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Total amount must be positive");
        }
        if (inputs == null || inputs.isEmpty()) {
            throw new ValidationException("At least one split input is required");
        }

        Set<String> uniqueUsers = new HashSet<>();
        for (SplitInput input : inputs) {
            if (input == null) {
                throw new ValidationException("Split input cannot be null");
            }
            if (input.getUserId() == null || input.getUserId().isBlank()) {
                throw new ValidationException("Split userId cannot be blank");
            }
            if (!uniqueUsers.add(input.getUserId())) {
                throw new ValidationException("Duplicate user in split inputs: " + input.getUserId());
            }
        }
    }

    default BigDecimal scale(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
