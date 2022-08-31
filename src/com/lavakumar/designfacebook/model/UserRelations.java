package com.lavakumar.designfacebook.model;

import java.util.HashSet;

public class UserRelations {
    private final HashSet<Integer> userFollower;
    private final HashSet<Integer> userFollowing;

    public UserRelations(){
        userFollower = new HashSet<>();
        userFollowing = new HashSet<>();
    }

    public HashSet<Integer> getUserFollower() {
        return userFollower;
    }

    public HashSet<Integer> getUserFollowing() {
        return userFollowing;
    }
}
