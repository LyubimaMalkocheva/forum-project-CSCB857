package com.forumsystem.modelhelpers;

public class SwaggerConstantHelper {

    //----------------GENERAL CONSTANTS---------------------------
    public static final String AUTHORIZATION = "Authorization";
    public static final String ONLY_BY_ADMINS = " This operation can be done only by Admins.";
    public static final String ONLY_BY_LOGGED_USERS = " This operation can be performed by logged users.";
    public static final String ONLY_BY_ADMINS_AND_CREATOR = " This operation can be performed only by admins or the user that created the";
    public static final String PROFILE = " profile";
    public static final String POST = " post";
    public static final String COMMENT = " comment";

    //-----------------USERS CONSTANTS----------------------------

    public static final String GET_ALL_USERS_SUMMARY = "Get all users";
    public static final String GET_ALL_USERS_DESCRIPTION = "Returns all registered users.";
    public static final String COUNT_USERS_SUMMARY = "Count users";
    public static final String COUNT_USERS_DESCRIPTION = "Returns the total number of registered users";
    public static final String GET_USER_BY_ID_SUMMARY = "Get user by ID";
    public static final String GET_USER_BY_ID_DESCRIPTION = "Returns a user by searching by its ID.";
    public static final String GET_USER_POSTS_SUMMARY = "Get all user posts";
    public static final String GET_USER_POSTS_DESCRIPTION = "Returns all posts of a user.";
    public static final String CREATE_USER_SUMMARY = "Create a new user";
    public static final String CREATE_USER_DESCRIPTION = "Registers a new user when valid details are provided.";
    public static final String UPDATE_USER_SUMMARY = "Update user";
    public static final String UPDATE_USER_DESCRIPTION = "Updates the details of a user.";
    public static final String DELETE_USER_SUMMARY = "Delete user";
    public static final String DELETE_USER_DESCRIPTION = "Deletes user profile.";
    public static final String BLOCK_USER_SUMMARY = "Block user";
    public static final String BLOCK_USER_DESCRIPTION = "Block user profile.";
    public static final String UNBLOCK_USER_SUMMARY = "Unblock user";
    public static final String UNBLOCK_USER_DESCRIPTION = "Unblock user profile.";

    //------------POSTS CONSTANTS---------------------------------

    public static final String GET_POSTS_SUMMARY = "Get all Posts";
    public static final String GET_POSTS_DESCRIPTION = "Returns all posts. This operation can be performed only by admins." +
            " The posts can be filtered or sorted by: Title, likes, dislikes, tag name.";
    public static final String GET_POST_BY_ID_SUMMARY = "Get post by Id";
    public static final String GET_POST_BY_ID_DESCRIPTION = "Returns a post by searching by its Id.";
    public static final String CREATE_POST_SUMMARY = "Create new post";
    public static final String CREATE_POST_DESCRIPTION = "Creates a new post. Blocked users cannot create new posts.";
    public static final String CREATE_COMMENT_SUMMARY = "Add a comment to a post";
    public static final String CREATE_COMMENT_DESCRIPTION = "Adds a new comment to an existing post. Blocked users cannot add comments.";
    public static final String UPDATE_POST_SUMMARY = "Update post";
    public static final String UPDATE_POST_DESCRIPTION = "Updates an existing post. Blocked users cannot update posts.";
    public static final String UPDATE_COMMENT_SUMMARY = "Update comment";
    public static final String UPDATE_COMMENT_DESCRIPTION = "Updates an existing comment. Blocked users cannot update comments.";
    public static final String LIKE_POST_SUMMARY = "Like post";
    public static final String LIKE_POST_DESCRIPTION = "Likes an existing post. The like operation can be performed only" +
            " once on a post. If the post has been disliked, by performing the like operation the dislike is removed";
    public static final String DISLIKE_POST_SUMMARY = "Dislike post";
    public static final String DISLIKE_POST_DESCRIPTION = "Dislikes an existing post. The dislike operation can be " +
            "performed only once on a post. If the post has been liked, by performing the dislike operation the " +
            "like is removed";
    public static final String DELETE_POST_SUMMARY = "Delete post";
    public static final String DELETE_POST_DESCRIPTION = "Deletes an existing post.";
    public static final String DELETE_COMMENT_SUMMARY = "Delete comment";
    public static final String DELETE_COMMENT_DESCRIPTION = "Deletes an existing comment";
}
