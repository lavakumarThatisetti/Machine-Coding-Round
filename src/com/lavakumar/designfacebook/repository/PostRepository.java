package com.lavakumar.designfacebook.repository;

import com.lavakumar.designfacebook.model.Post;
import com.lavakumar.designfacebook.model.User;

import java.util.HashMap;

public class PostRepository {

    private final HashMap<Integer, Post> postHashMap = new HashMap<>();

    public Post savePost(Post post){

        if(postHashMap.get(post.getPostId())!=null){
            postHashMap.put(post.getPostId(),post);
        }
        return post;
    }
    public Post deletePost(int postId){
        if(postHashMap.get(postId)!=null){
            return postHashMap.remove(postId);
        }
        return null;
    }
    public Post getPost(int postId){
        return postHashMap.get(postId);
    }
}
