package validator;

import dto.AddExpenseRequest;
import exception.ValidationException;
import model.SplitInput;

import java.util.HashSet;
import java.util.Set;

public class ExpenseRequestValidator {

    public void validate(AddExpenseRequest request) {
        if (request == null) {
            throw new ValidationException("AddExpenseRequest cannot be null");
        }
        if (request.expenseId() == null || request.expenseId().isBlank()) {
            throw new ValidationException("Expense id cannot be blank");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new ValidationException("Expense title cannot be blank");
        }
        if (request.totalAmount() == null || request.totalAmount().signum() <= 0) {
            throw new ValidationException("Expense total amount must be positive");
        }
        if (request.paidByUserId() == null || request.paidByUserId().isBlank()) {
            throw new ValidationException("Paid by userId cannot be blank");
        }
        if (request.splitType() == null) {
            throw new ValidationException("Split type cannot be null");
        }
        if (request.splitInputs().isEmpty()) {
            throw new ValidationException("Split inputs cannot be empty");
        }

        Set<String> uniqueUsers = new HashSet<>();

        for (SplitInput input : request.splitInputs()) {
            if (input == null) {
                throw new ValidationException("Split input cannot be null");
            }
            if (input.getUserId() == null || input.getUserId().isBlank()) {
                throw new ValidationException("Split userId cannot be blank");
            }
            if (!uniqueUsers.add(input.getUserId())) {
                throw new ValidationException("Duplicate participant in split inputs: " + input.getUserId());
            }
        }
    }
}
