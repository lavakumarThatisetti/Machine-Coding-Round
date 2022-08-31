package com.lavakumar.designfacebook.service;

import com.lavakumar.designfacebook.model.Post;
import com.lavakumar.designfacebook.model.UserRelations;
import com.lavakumar.designfacebook.repository.PostRepository;
import com.lavakumar.designfacebook.repository.UserPostRepository;
import javafx.geometry.Pos;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class NewsFeedService {

    private final UserService userService;
    private final UserRelationsService userRelationsService;
    private final UserPostRepository userPostRepository;
    private final PostService postService;

    public NewsFeedService(UserService userService,
                           UserRelationsService userRelationsService,
                           UserPostRepository userPostRepository,
                           PostService postService){
        this.userService = userService;
        this.userRelationsService = userRelationsService;
        this.userPostRepository = userPostRepository;
        this.postService = postService;
    }

    public Collection<Post> getNewsFeed(int userId) {
        TreeMap<Integer, Post> latestNewsFeed =  new TreeMap<>();
        // Check UserId Present or not;
        if(userService.getUser(userId)!=null){
            UserRelations userRelations = userRelationsService.getUserRelations(userId);
            HashSet<Integer> userFollowing = userRelations.getUserFollowing();
            for(Integer followerId: userFollowing){
                Set<Integer> allPostIds = userPostRepository.getAllPostsOfUser(followerId);
                for(Integer postId: allPostIds)
                    latestNewsFeed.put(postId,postService.getPost(postId));
            }
        }
        return latestNewsFeed.values();
    }

    public List<Post> getNewsFeedPaginated(int userId, int pageNumber){
        return null;
    }
}
