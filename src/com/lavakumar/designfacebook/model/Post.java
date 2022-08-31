package com.lavakumar.designfacebook.model;

import java.util.Date;

public class Post {
    private final int postId;
    private final int userId;
    private final String postTitle;
    private final Date postCreateTimeStamp;
    private Date postUpdateTimeStamp;

    public Post(int postId, int userId, String postTitle, Date postCreateTimeStamp) {
        this.postId = postId;
        this.userId = userId;
        this.postTitle = postTitle;
        this.postCreateTimeStamp = postCreateTimeStamp;
    }

    public int getPostId() {
        return postId;
    }

    public int getUserId() {
        return userId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public Date getPostCreateTimeStamp() {
        return postCreateTimeStamp;
    }

    public Date getPostUpdateTimeStamp() {
        return postUpdateTimeStamp;
    }

    public void setPostUpdateTimeStamp(Date postUpdateTimeStamp) {
        this.postUpdateTimeStamp = postUpdateTimeStamp;
    }
}
