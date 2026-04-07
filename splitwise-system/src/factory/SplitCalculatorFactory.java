package factory;

import exception.ValidationException;
import model.SplitType;
import model.split.EqualSplitCalculator;
import model.split.ExactSplitCalculator;
import model.split.PercentSplitCalculator;
import model.split.SplitCalculator;

import java.util.EnumMap;
import java.util.Map;

public class SplitCalculatorFactory {
    private final Map<SplitType, SplitCalculator> calculators;

    public SplitCalculatorFactory() {
        this.calculators = new EnumMap<>(SplitType.class);
        this.calculators.put(SplitType.EQUAL, new EqualSplitCalculator());
        this.calculators.put(SplitType.EXACT, new ExactSplitCalculator());
        this.calculators.put(SplitType.PERCENT, new PercentSplitCalculator());
    }

    public SplitCalculator getCalculator(SplitType splitType) {
        if (splitType == null) {
            throw new ValidationException("Split type cannot be null");
        }

        SplitCalculator calculator = calculators.get(splitType);
        if (calculator == null) {
            throw new ValidationException("Unsupported split type: " + splitType);
        }
        return calculator;
    }
}
