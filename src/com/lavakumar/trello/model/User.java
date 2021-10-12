package com.lavakumar.trello.model;


import java.util.UUID;

public class User {
    UUID userId;
    String userName;
    String email;

    public User(String userName, String email) {
        this.userId = UUID.randomUUID();
        this.userName = userName;
        this.email = email;
    }

    public UUID getUserId() {
        return userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
