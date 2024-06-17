package com.forumsystem.repositories.contracts;

import com.forumsystem.modelhelpers.PostModelFilterOptions;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;

import java.util.List;

public interface PostRepository {
    List<Post> getAll(PostModelFilterOptions filterOptions);
    List<Post> getAllForAdmin(PostModelFilterOptions postFilter);

    List<Post> getTopTenCommentedPosts();

    List<Post> getTenNewestPosts();

    Long getPostCount();

    Post getById(int id);

    void create(User user, Post post);

    Post update(Post post);

    void delete(Post post);

    void likePost(int postId, int userId);

    void dislikePost(int postId, int userId);

}
