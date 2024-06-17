package com.forumsystem.services;

import com.forumsystem.modelmappers.CommentResponseMapper;
import com.forumsystem.models.Comment;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.CommentResponseDto;
import com.forumsystem.repositories.contracts.CommentRepository;
import com.forumsystem.repositories.contracts.PostRepository;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.services.contracts.CommentService;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.forumsystem.modelhelpers.ModelConstantHelper.COMMENTS;
import static com.forumsystem.modelhelpers.ModelConstantHelper.UNAUTHORIZED_EDIT_ERROR_MESSAGE;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final CommentResponseMapper commentResponseMapper;


    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, CommentResponseMapper commentResponseMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentResponseMapper = commentResponseMapper;
    }

    @Override
    public List<CommentResponseDto> getAll(int postId) {
        List<Comment> comments = commentRepository.getAll(postId);
        List<CommentResponseDto> responseDtos = comments.stream()
                .map(comment -> commentResponseMapper.convertToDTO(comment))
                .collect(Collectors.toList());

        return responseDtos;
    }

    @Override
    public Comment getById(int id) {
        return commentRepository.getById(id);
    }

    @Override
    public void create(int user_id, Comment comment, int post_id) {
        User user = userRepository.get(user_id);
        Post post = postRepository.getById(post_id);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.create(comment);
    }

    @Override
    public void update(User user, Comment comment) {
        if(!comment.getUser().equals(user) &&
                !userRepository.checkIfAdmin(user.getUserId())){
            throw new UnauthorizedOperationException(
                    String.format(UNAUTHORIZED_EDIT_ERROR_MESSAGE, COMMENTS));
        }
         commentRepository.update(comment);
    }

    @Override
    public void delete(User user, Comment comment) {
        if(!comment.getUser().equals(user) && !userRepository.checkIfAdmin(user.getUserId())){
            throw new UnauthorizedOperationException(
                    String.format(UNAUTHORIZED_EDIT_ERROR_MESSAGE, COMMENTS));
        }
        comment.setArchived(true);

        commentRepository.delete(comment);
    }
}
