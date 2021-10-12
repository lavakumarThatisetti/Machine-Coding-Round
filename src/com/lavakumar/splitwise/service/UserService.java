package com.lavakumar.splitwise.service;

import com.lavakumar.splitwise.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    List<User> users;

    public UserService(List<User> users){
        this.users = users;
    }

    public Optional<User> getUser(String userName){
        return users.stream().filter(u->u.getUserName().equals(userName)).findFirst();
    }
}
