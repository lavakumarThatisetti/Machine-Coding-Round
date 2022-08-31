package com.lavakumar.designfacebook.service;

import com.lavakumar.designfacebook.model.UserRelations;


import java.util.HashMap;
import java.util.Map;

public class UserRelationsService {

    Map<Integer, UserRelations>  userRelationsMap;

    public UserRelationsService(){
        this.userRelationsMap = new HashMap<>();
    }


    public void FollowUser(int userId, int followingUserId) {
        UserRelations userRelations = userRelationsMap.get(userId);
        UserRelations followingUserRelations = userRelationsMap.get(followingUserId);

        userRelations.getUserFollowing().add(followingUserId);
        followingUserRelations.getUserFollower().add(userId);
    }

    public void UnFollowUser(int userId, int followingUserId){
        UserRelations userRelations = userRelationsMap.get(userId);
        UserRelations followingUserRelations = userRelationsMap.get(followingUserId);

        userRelations.getUserFollowing().remove(followingUserId);
        followingUserRelations.getUserFollower().remove(userId);
    }

    public UserRelations getUserRelations(int userId){
        return userRelationsMap.get(userId);
    }



}
