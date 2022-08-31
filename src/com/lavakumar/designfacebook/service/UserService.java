package com.lavakumar.designfacebook.service;

import com.lavakumar.designfacebook.model.User;
import com.lavakumar.designfacebook.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User createUser(User user){
        return userRepository.saveUser(user);
    }

    public User getUser(int userId){
        return userRepository.getUser(userId);
    }

}
