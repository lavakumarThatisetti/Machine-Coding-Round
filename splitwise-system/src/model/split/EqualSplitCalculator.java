package model.split;

import model.Share;
import model.SplitInput;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EqualSplitCalculator implements SplitCalculator {

    @Override
    public List<Share> calculate(BigDecimal totalAmount, List<SplitInput> inputs) {
        validateCommon(totalAmount, inputs);

        int size = inputs.size();
        BigDecimal baseShare = totalAmount
                .divide(BigDecimal.valueOf(size), 2, RoundingMode.DOWN);

        List<Share> shares = new ArrayList<>();
        BigDecimal assigned = BigDecimal.ZERO;

        for (int i = 0; i < size; i++) {
            SplitInput input = inputs.get(i);
            BigDecimal shareAmount = (i == size - 1)
                    ? totalAmount.subtract(assigned)
                    : baseShare;

            shareAmount = scale(shareAmount);
            shares.add(new Share(input.getUserId(), shareAmount));
            assigned = assigned.add(shareAmount);
        }

        return shares;
    }
}
