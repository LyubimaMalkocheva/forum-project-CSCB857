package com.forumsystem.models;

import jakarta.persistence.*;
    @Entity
    @Table(name = "user_likes")
    public class UserLikes {

        @EmbeddedId
        private UserLikesId id;

        @ManyToOne
        @JoinColumn(name = "user_id", updatable = false, insertable = false)
        private User user;

        @ManyToOne
        @JoinColumn(name = "post_id", updatable = false, insertable = false)
        private Post post;

        @Column(name = "is_liked")
        private boolean isLiked;

        @Column(name = "is_disliked")
        private boolean isDisliked;

        public UserLikes() {
        }

        public UserLikes(User user, Post post, boolean isLiked, boolean isDisliked) {
            this.id = new UserLikesId(user.getUserId(), post.getPostId());
            this.user = user;
            this.post = post;
            this.isLiked = isLiked;
            this.isDisliked = isDisliked;
        }

        public UserLikesId getId() {
            return id;
        }

        public void setId(UserLikesId id) {
            this.id = id;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public boolean isDisliked() {
            return isDisliked;
        }

        public void setDisliked(boolean disliked) {
            isDisliked = disliked;
        }
    }
