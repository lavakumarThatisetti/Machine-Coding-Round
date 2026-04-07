package service;

import dto.AddExpenseRequest;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import factory.SplitCalculatorFactory;
import model.Expense;
import model.Group;
import model.Share;
import model.SplitInput;
import model.split.SplitCalculator;
import repository.BalanceLedgerRepository;
import repository.ExpenseRepository;
import repository.GroupRepository;
import repository.UserRepository;
import validator.ExpenseRequestValidator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final BalanceLedgerRepository balanceLedgerRepository;
    private final SplitCalculatorFactory splitCalculatorFactory;
    private final ExpenseRequestValidator expenseRequestValidator;
    private final LockManager lockManager;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            BalanceLedgerRepository balanceLedgerRepository,
            SplitCalculatorFactory splitCalculatorFactory,
            ExpenseRequestValidator expenseRequestValidator,
            LockManager lockManager
    ) {
        this.expenseRepository = Objects.requireNonNull(expenseRepository, "ExpenseRepository cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
        this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null");
        this.balanceLedgerRepository = Objects.requireNonNull(balanceLedgerRepository, "BalanceLedgerRepository cannot be null");
        this.splitCalculatorFactory = Objects.requireNonNull(splitCalculatorFactory, "SplitCalculatorFactory cannot be null");
        this.expenseRequestValidator = Objects.requireNonNull(expenseRequestValidator, "ExpenseRequestValidator cannot be null");
        this.lockManager = Objects.requireNonNull(lockManager, "LockManager cannot be null");
    }

    public Expense addExpense(AddExpenseRequest request) {
        expenseRequestValidator.validate(request);

        ReentrantLock lock = lockManager.getLock(buildLockKey(request));
        lock.lock();
        try {
            if (expenseRepository.findById(request.expenseId()).isPresent()) {
                throw new DuplicateEntityException("Expense already exists with id: " + request.expenseId());
            }

            validateUsersExist(request);
            validateGroupRules(request);

            SplitCalculator calculator = splitCalculatorFactory.getCalculator(request.splitType());
            List<Share> shares = calculator.calculate(request.totalAmount(), request.splitInputs());

            validateShareTotal(request, shares);
            validatePayerPresentIfRequired(request, shares);

            Expense expense = new Expense(
                    request.expenseId(),
                    request.title(),
                    request.totalAmount(),
                    request.paidByUserId(),
                    request.groupId(),
                    shares,
                    Instant.now()
            );

            expenseRepository.save(expense);

            if (request.groupId() != null) {
                Group group = groupRepository.findById(request.groupId())
                        .orElseThrow(() -> new EntityNotFoundException("Group not found: " + request.groupId()));
                group.addExpense(expense.getId());
                groupRepository.save(group);
            }

            applyLedgerUpdates(expense);

            return expense;
        } finally {
            lock.unlock();
        }
    }

    public Expense getExpense(String expenseId) {
        if (expenseId == null || expenseId.isBlank()) {
            throw new ValidationException("Expense id cannot be blank");
        }

        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + expenseId));
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesForGroup(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            throw new ValidationException("Group id cannot be blank");
        }
        return expenseRepository.findByGroupId(groupId);
    }

    private void validateUsersExist(AddExpenseRequest request) {
        if (!userRepository.existsById(request.paidByUserId())) {
            throw new EntityNotFoundException("Payer user not found: " + request.paidByUserId());
        }

        for (SplitInput input : request.splitInputs()) {
            if (!userRepository.existsById(input.getUserId())) {
                throw new EntityNotFoundException("Participant user not found: " + input.getUserId());
            }
        }
    }

    private void validateGroupRules(AddExpenseRequest request) {
        if (request.groupId() == null) {
            return;
        }

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + request.groupId()));

        if (!group.hasMember(request.paidByUserId())) {
            throw new ValidationException("Payer must be a member of the group");
        }

        for (SplitInput input : request.splitInputs()) {
            if (!group.hasMember(input.getUserId())) {
                throw new ValidationException("Participant " + input.getUserId() + " is not a member of group " + request.groupId());
            }
        }
    }

    private void validateShareTotal(AddExpenseRequest request, List<Share> shares) {
        if (shares == null || shares.isEmpty()) {
            throw new ValidationException("Calculated shares cannot be empty");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Share share : shares) {
            total = total.add(share.amount());
        }

        if (total.compareTo(request.totalAmount()) != 0) {
            throw new ValidationException("Calculated share total must match expense total amount");
        }
    }

    private void validatePayerPresentIfRequired(AddExpenseRequest request, List<Share> shares) {
        boolean present = false;
        for (Share share : shares) {
            if (share.userId().equals(request.paidByUserId())) {
                present = true;
                break;
            }
        }

        if (!present) {
            throw new ValidationException("Payer must be included in expense participants");
        }
    }

    private void applyLedgerUpdates(Expense expense) {
        for (Share share : expense.getShares()) {
            if (share.userId().equals(expense.getPaidByUserId())) {
                continue;
            }

            if (share.amount().signum() == 0) {
                continue;
            }

            balanceLedgerRepository.addDebt(
                    expense.getGroupId(),
                    share.userId(),
                    expense.getPaidByUserId(),
                    share.amount()
            );
        }
    }

    private String buildLockKey(AddExpenseRequest request) {
        if (request.groupId() != null && !request.groupId().isBlank()) {
            return "GROUP_EXPENSE_" + request.groupId();
        }

        List<String> participants = new ArrayList<>();
        participants.add(request.paidByUserId());
        for (SplitInput input : request.splitInputs()) {
            participants.add(input.getUserId());
        }

        participants.sort(Comparator.naturalOrder());

        return "DIRECT_EXPENSE_" + String.join("_", participants);
    }
}
