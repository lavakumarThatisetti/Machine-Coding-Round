package repository.impl;


import model.Expense;
import repository.ExpenseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryExpenseRepository implements ExpenseRepository {
    private final ConcurrentMap<String, Expense> storage = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<Expense>> groupToExpensesMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<Expense>> userToExpensesMap = new ConcurrentHashMap<>();


    @Override
    public void save(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }
        storage.put(expense.getId(), expense);

        if(expense.isGroupExpense()) {
            groupToExpensesMap.putIfAbsent(expense.getGroupId(), new ArrayList<>());
            groupToExpensesMap.get(expense.getGroupId()).add(expense);
        }
        userToExpensesMap.putIfAbsent(expense.getPaidByUserId(), new ArrayList<>());

        userToExpensesMap.get(expense.getPaidByUserId()).add(expense);
    }

    @Override
    public Optional<Expense> findById(String expenseId) {
        if (expenseId == null || expenseId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(expenseId));
    }

    @Override
    public List<Expense> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Expense> findByGroupId(String groupId) {

        return new ArrayList<>(groupToExpensesMap.getOrDefault(groupId, List.of()));
    }

    @Override
    public List<Expense> findByPaidByUserId(String userId) {
        return new ArrayList<>(userToExpensesMap.getOrDefault(userId, List.of()));
    }
}