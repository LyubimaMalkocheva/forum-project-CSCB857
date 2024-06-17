package com.forumsystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class UserLikesId implements Serializable {
    @Column(name = "user_id")
    private int userId;

    @Column(name = "post_id")
    private int postId;

    public UserLikesId() {
    }

    public UserLikesId(int userId, int postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}
