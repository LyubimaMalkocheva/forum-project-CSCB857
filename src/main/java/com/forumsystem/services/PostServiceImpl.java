package com.forumsystem.services;

import com.forumsystem.modelhelpers.PostModelFilterOptions;
import com.forumsystem.models.Comment;
import com.forumsystem.repositories.contracts.PostRepository;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.services.contracts.CommentService;
import com.forumsystem.services.contracts.PostService;
import com.forumsystem.services.contracts.TagService;
import com.forumsystem.services.contracts.UserService;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.forumsystem.modelhelpers.ModelConstantHelper.*;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           CommentService commentService,
                           TagService tagService,
                           UserService userService
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentService = commentService;
        this.tagService = tagService;
        this.userService = userService;
    }


    @Override
    public List<Post> getAll(User user, PostModelFilterOptions postFilter) {

        return postRepository.getAll(postFilter);
    }

    @Override
    public List<Post> getAllForAdmin(User user, PostModelFilterOptions postFilter) {
        userService.checkIfAdmin(user);
        return postRepository.getAllForAdmin(postFilter);
    }

    @Override
    public List<Post> getTopTenCommentedPosts() {
        return postRepository.getTopTenCommentedPosts();
    }

    @Override
    public List<Post> getTenNewestPosts() {
        return postRepository.getTenNewestPosts();
    }

    @Override
    public Long getPostCount() {
        return postRepository.getPostCount();
    }

    @Override
    public Post getById(User user, int id) {
       Post post = postRepository.getById(id);
       if (post.isArchived() && !userService.checkIfAdmin(user.getUserId())){
           throw new EntityNotFoundException("post", "id", String.valueOf(post.getPostId()));
       }
       return post;
    }

    @Override
    public void create(User user, Post post) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(
                    String.format(BLOCKED_USER_ERROR_MESSAGE, POST, CREATION));
        }

        postRepository.create(user, post);
    }

    @Override
    public void createComment(User user, Comment comment, int postId) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(
                    String.format(BLOCKED_USER_ERROR_MESSAGE, COMMENT, CREATION));
        }

        commentService.create(user.getUserId(), comment, postId);
    }

    @Override
    public Post updatePost(User user, Post postToBeUpdated) {

        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(
                    String.format(BLOCKED_USER_ERROR_MESSAGE, POST, EDITING));
        }

        if (!postToBeUpdated.getCreatedBy().equals(user) &&
                !userRepository.checkIfAdmin(user.getUserId())) {
            throw new UnauthorizedOperationException(
                    String.format(UNAUTHORIZED_EDIT_ERROR_MESSAGE, POSTS));
        }

        tagService.create(postToBeUpdated.getPostTags());
        return postRepository.update(postToBeUpdated);
    }

    @Override
    public void updateComment(User user, Comment comment, int post_id) {
        if (user.isBlocked()) {
            throw new UnauthorizedOperationException(
                    String.format(BLOCKED_USER_ERROR_MESSAGE, COMMENT, EDITING));
        }

        commentService.update(user, comment);
    }

    @Override
    public void likePost(Post post, User user) {
        postRepository.likePost(post.getPostId(), user.getUserId());
    }

    @Override
    public void dislikePost(Post post, User user) {
        postRepository.dislikePost(post.getPostId(), user.getUserId());
    }

    @Override
    public void delete(User user, int id) {

        Post postToBeDeleted = postRepository.getById(id);

        if (!postToBeDeleted.getCreatedBy().equals(user) &&
                !userRepository.checkIfAdmin(user.getUserId())) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_DELETION_ERROR_MESSAGE);
        }

        postToBeDeleted.setIsArchived(true);

        postRepository.delete(postToBeDeleted);
    }

    @Override
    public void deleteComment(User user, int postId, int commentId) {
        Comment comment = commentService.getById(commentId);
        commentService.delete(user, comment);
    }

}
