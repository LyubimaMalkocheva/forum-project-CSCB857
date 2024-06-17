package com.forumsystem.services.contracts;

import com.forumsystem.modelhelpers.PostModelFilterOptions;
import com.forumsystem.models.Comment;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;

import java.util.List;

public interface PostService {

    List<Post> getAll(User user, PostModelFilterOptions postFilter);

    List<Post> getAllForAdmin(User user, PostModelFilterOptions postFilter);

    List<Post> getTopTenCommentedPosts();

    List<Post> getTenNewestPosts();

    Long getPostCount();

    Post getById(User user, int id);

    void create(User user, Post post);

    void createComment(User user, Comment comment, int postId);

    Post updatePost(User user, Post post);

    void updateComment(User user, Comment comment, int postId);

    void likePost(Post post, User user);

    void dislikePost(Post post, User user);

    void delete(User user, int id);

    void deleteComment(User user, int postId, int commentId);
}
