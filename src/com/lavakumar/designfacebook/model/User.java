package com.lavakumar.designfacebook.model;

public class User {
    private final int userId;
    private final int userName;

    public User(int userId, int userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public int getUserName() {
        return userName;
    }
}
