package dto;

import model.SplitInput;
import model.SplitType;

import java.math.BigDecimal;
import java.util.List;

/**
 * @param groupId nullable for direct expense
 */
public record AddExpenseRequest(String expenseId, String title, BigDecimal totalAmount, String paidByUserId,
                                String groupId, SplitType splitType, List<SplitInput> splitInputs) {
    public AddExpenseRequest(
            String expenseId,
            String title,
            BigDecimal totalAmount,
            String paidByUserId,
            String groupId,
            SplitType splitType,
            List<SplitInput> splitInputs
    ) {
        this.expenseId = expenseId;
        this.title = title;
        this.totalAmount = totalAmount;
        this.paidByUserId = paidByUserId;
        this.groupId = groupId;
        this.splitType = splitType;
        this.splitInputs = splitInputs == null ? List.of() : List.copyOf(splitInputs);
    }
}
