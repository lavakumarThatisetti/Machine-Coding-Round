package model.split;


import exception.ValidationException;
import model.Share;
import model.SplitInput;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PercentSplitCalculator implements SplitCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Override
    public List<Share> calculate(BigDecimal totalAmount, List<SplitInput> inputs) {
        validateCommon(totalAmount, inputs);

        BigDecimal totalPercent = BigDecimal.ZERO;
        for (SplitInput input : inputs) {
            totalPercent = totalPercent.add(input.getValue());
        }

        if (scale(totalPercent).compareTo(scale(HUNDRED)) != 0) {
            throw new ValidationException("Percent splits must sum up to 100");
        }

        List<Share> shares = new ArrayList<>();
        BigDecimal assigned = BigDecimal.ZERO;

        for (int i = 0; i < inputs.size(); i++) {
            SplitInput input = inputs.get(i);
            BigDecimal shareAmount;

            if (i == inputs.size() - 1) {
                shareAmount = totalAmount.subtract(assigned);
            } else {
                shareAmount = totalAmount
                        .multiply(input.getValue())
                        .divide(HUNDRED, 2, RoundingMode.HALF_UP);
            }

            shareAmount = scale(shareAmount);
            shares.add(new Share(input.getUserId(), shareAmount));
            assigned = assigned.add(shareAmount);
        }

        return shares;
    }
}
