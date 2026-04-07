package repository;


import model.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
    void save(Expense expense);

    Optional<Expense> findById(String expenseId);

    List<Expense> findAll();

    List<Expense> findByGroupId(String groupId);

    List<Expense> findByPaidByUserId(String userId);
}