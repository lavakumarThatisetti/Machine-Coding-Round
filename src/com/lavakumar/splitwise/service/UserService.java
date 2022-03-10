package com.lavakumar.splitwise.service;

import com.lavakumar.splitwise.model.User;
import com.lavakumar.splitwise.repository.ExpenseRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    ExpenseRepository expenseRepository;

    public UserService(ExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }

    public void addUser(User user){
        expenseRepository.addUser(user);
    }
    public User getUser(String userName){
        return expenseRepository.getUser(userName);
    }
}
