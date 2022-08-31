package com.lavakumar.designfacebook.service;

import com.lavakumar.designfacebook.model.Post;
import com.lavakumar.designfacebook.repository.PostRepository;
import com.lavakumar.designfacebook.repository.UserPostRepository;
import com.lavakumar.designfacebook.repository.UserRepository;


public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserPostRepository userPostRepository;

    public PostService(PostRepository postRepository,UserRepository userRepository, UserPostRepository userPostRepository ){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userPostRepository = userPostRepository;
    }

    public Post createPost(Post post){
        // Check User
        if(userRepository.getUser(post.getUserId())!=null){
            Post savedPost = postRepository.savePost(post);
            userPostRepository.savePostToUser(savedPost);
            return savedPost;
        }
        return null;
    }

    public boolean deletePost(int userId, int postId){
        if(postRepository.deletePost(postId) != null){
            return userPostRepository.deletePostOfUser(userId,postId);
        }
        return false;
    }

    public Post getPost(int postId){
        return postRepository.getPost(postId);
    }
}
