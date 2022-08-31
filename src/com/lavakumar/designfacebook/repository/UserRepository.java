package com.lavakumar.designfacebook.repository;

import com.lavakumar.designfacebook.model.User;

import java.util.HashMap;

public class UserRepository {
    private final HashMap<Integer, User> userMap = new HashMap<>();

    public User saveUser(User user){
        if(userMap.get(user.getUserId())!=null){
            userMap.put(user.getUserId(),user);
        }
        return user;
    }

    public User getUser(int userId){
        return userMap.get(userId);
    }
}
