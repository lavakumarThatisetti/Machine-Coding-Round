package com.lavakumar.splitwise.repository;

import com.lavakumar.splitwise.model.ExpenseType;
import com.lavakumar.splitwise.model.User;
import com.lavakumar.splitwise.model.expense.Expense;
import com.lavakumar.splitwise.model.expense.ExpenseData;
import com.lavakumar.splitwise.model.split.Split;
import com.lavakumar.splitwise.service.ExpenseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseRepository {
    List<Expense> expenses;
    Map<String, User> userMap;
    Map<String, Map<String, Double>> balanceSheet;

    public ExpenseRepository() {
        expenses = new ArrayList<>();
        userMap = new HashMap<>();
        balanceSheet = new HashMap<>();
    }

    public void addUser(User user) {
        userMap.put(user.getUserName(), user);
        balanceSheet.put(user.getUserName(), new HashMap<>());
    }

    public User getUser(String userName){
        return userMap.get(userName);
    }

    public void addExpense(ExpenseType expenseType, double amount, String paidBy, List<Split> splits, ExpenseData expenseData) {
        Expense expense = ExpenseService.createExpense(expenseType, amount, userMap.get(paidBy), splits, expenseData);
        expenses.add(expense);
        assert expense != null;
        for (Split split : expense.getSplits()) {
            String paidTo = split.getUser().getUserName();

            Map<String, Double> balances = balanceSheet.get(paidBy);
            if (!balances.containsKey(paidTo)) {
                balances.put(paidTo, 0.0);
            }
            balances.put(paidTo, balances.get(paidTo) + split.getAmount());

            balances = balanceSheet.get(paidTo);
            if (!balances.containsKey(paidBy)) {
                balances.put(paidBy, 0.0);
            }
            balances.put(paidBy, balances.get(paidBy) - split.getAmount());
        }
    }

    public List<String> getBalance(String userName) {
        List<String> balances = new ArrayList<>();
        for (Map.Entry<String, Double> userBalance : balanceSheet.get(userName).entrySet()) {
            if (userBalance.getValue() != 0) {
                balances.add(checkSign(userName, userBalance.getKey(), userBalance.getValue()));
            }
        }
        return balances;
    }

    public List<String> getBalances() {
        List<String> balances = new ArrayList<>();
        for (Map.Entry<String, Map<String, Double>> allBalances : balanceSheet.entrySet()) {
            for (Map.Entry<String, Double> userBalance : allBalances.getValue().entrySet()) {
                if (userBalance.getValue() > 0) {
                    balances.add(checkSign(allBalances.getKey(), userBalance.getKey(), userBalance.getValue()));
                }
            }
        }
        return balances;
    }

    private String checkSign(String user1, String user2, double amount) {
        String user1Name = userMap.get(user1).getUserName();
        String user2Name = userMap.get(user2).getUserName();
        if (amount < 0) {
            return  user1Name + " owes " + user2Name + ": " + Math.abs(amount);
        } else if (amount > 0) {
            return user2Name + " owes " + user1Name + ": " + Math.abs(amount);
        }
        return "";
    }
}
