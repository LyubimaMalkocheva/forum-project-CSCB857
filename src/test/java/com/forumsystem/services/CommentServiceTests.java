package com.forumsystem.services;

import com.forumsystem.modelmappers.CommentResponseMapper;
import com.forumsystem.models.Comment;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.CommentResponseDto;
import com.forumsystem.repositories.contracts.CommentRepository;
import com.forumsystem.repositories.contracts.PostRepository;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentResponseMapper commentResponseMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void getAll_Should_ReturnCommentResponseDtos_ForGivenPostId() {
        // Arrange
        int postId = 1;
        List<Comment> comments = Arrays.asList(new Comment(), new Comment());
        Mockito.when(commentRepository.getAll(postId)).thenReturn(comments);
        Mockito.when(commentResponseMapper.convertToDTO(any(Comment.class))).thenReturn(new CommentResponseDto());

        // Act
        List<CommentResponseDto> result = commentService.getAll(postId);

        // Assert
        Mockito.verify(commentRepository).getAll(postId);
        Mockito.verify(commentResponseMapper, Mockito.times(comments.size())).convertToDTO(any(Comment.class));
        Assertions.assertEquals(comments.size(), result.size());
    }

    @Test
    void getById_Should_ReturnComment_ForGivenId() {
        // Arrange
        int id = 1;
        Comment comment = new Comment();
        Mockito.when(commentRepository.getById(id)).thenReturn(comment);

        // Act
        Comment result = commentService.getById(id);

        // Assert
        Mockito.verify(commentRepository).getById(id);
        Assertions.assertEquals(comment, result);
    }

    @Test
    void create_Should_CreateAComment() {
        // Arrange
        int userId = 1;
        int postId = 1;
        Comment comment = new Comment();
        User user = new User();
        Post post = new Post();
        Mockito.when(userRepository.get(userId)).thenReturn(user);
        Mockito.when(postRepository.getById(postId)).thenReturn(post);

        // Act
        commentService.create(userId, comment, postId);

        // Assert
        Mockito.verify(commentRepository).create(any(Comment.class));
    }

    @Test
    void update_ShouldSucceed_WhenUserIsCommentCreator() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        Comment comment = new Comment();
        comment.setUser(user);

        // Act
        commentService.update(user, comment);

        // Assert
        Mockito.verify(commentRepository).update(comment);
    }

    @Test
    void delete_ShouldSucceed_WhenUserIsCommentCreator() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        Comment comment = new Comment();
        comment.setUser(user);

        // Act
        commentService.delete(user, comment);

        // Assert
        Mockito.verify(commentRepository).delete(comment);
        Assertions.assertTrue(comment.isArchived());
    }

    @Test
    void delete_ShouldThrowException_WhenUserIsNotAuthorized() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setUserId(4);
        User commentCreator = new User();
        commentCreator.setUserId(5);
        Comment comment = new Comment();
        comment.setUser(commentCreator);

        Mockito.when(userRepository.checkIfAdmin(unauthorizedUser.getUserId())).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> {
            commentService.delete(unauthorizedUser, comment);
        });
        Mockito.verify(commentRepository, Mockito.never()).delete(any(Comment.class));
    }
}
