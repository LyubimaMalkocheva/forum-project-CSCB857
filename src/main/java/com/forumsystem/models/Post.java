package com.forumsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;
    @Column(name = "total_comments")
    private int comments;

    @Column(name = "total_likes")
    private int likes;

    @Column(name = "total_dislikes")
    private int dislikes;

    @JsonIgnore
    @Column(name = "is_archived")
    private boolean isArchived;

    @Column(name = "date_time")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "posts_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> postTags;

    public Post() {
    }

    public Post(int postId, User createdBy, String title,
                String content, boolean isArchived, Set<Tag> postTags) {
        this.postId = postId;
        this.createdBy = createdBy;
        this.title = title;
        this.content = content;
        this.isArchived = isArchived;
        this.postTags = postTags;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setIsArchived(boolean archived) {
        isArchived = archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime dateTime) {
        this.createdAt = dateTime;
    }

    public Set<Tag> getPostTags() {
        return postTags;
    }

    public void setPostTags(Set<Tag> postTags) {
        this.postTags = postTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post post)) return false;
        return getPostId() == post.getPostId();
    }
}


