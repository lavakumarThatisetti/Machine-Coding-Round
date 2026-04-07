package model.split;

import exception.ValidationException;
import model.Share;
import model.SplitInput;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExactSplitCalculator implements SplitCalculator {

    @Override
    public List<Share> calculate(BigDecimal totalAmount, List<SplitInput> inputs) {
        validateCommon(totalAmount, inputs);

        List<Share> shares = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;

        for (SplitInput input : inputs) {
            BigDecimal amount = scale(input.getValue());
            shares.add(new Share(input.getUserId(), amount));
            sum = sum.add(amount);
        }

        if (scale(sum).compareTo(scale(totalAmount)) != 0) {
            throw new ValidationException("Exact split amounts must sum up to total amount");
        }

        return shares;
    }
}
