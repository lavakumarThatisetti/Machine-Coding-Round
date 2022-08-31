package com.lavakumar.designfacebook.repository;

import com.lavakumar.designfacebook.exception.UserPostException;
import com.lavakumar.designfacebook.model.Post;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class UserPostRepository {
    private final HashMap<Integer, Set<Integer>> userPostMap = new HashMap<>();

    public void savePostToUser(Post post){
        userPostMap.computeIfAbsent(post.getUserId(), k -> new TreeSet<>());
        userPostMap.get(post.getUserId()).add(post.getPostId());
    }
    public Boolean deletePostOfUser(int userId, int postId){
        if(userPostMap.get(userId)==null){
            throw new UserPostException("USER NOT FOUND");
        }
        return userPostMap.get(userId).remove(postId);
    }

    public Set<Integer> getAllPostsOfUser(int userId){
        Set<Integer> postIds = userPostMap.get(userId);
        return postIds;
    }
}
