package com.lavakumar.designfacebook.exception;

public class UserPostException extends RuntimeException{

    private final String message;

    public UserPostException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
