package com.forumsystem.controllers.rest;

import com.forumsystem.modelhelpers.AuthenticationHelper;
import com.forumsystem.modelhelpers.PostModelFilterOptions;
import com.forumsystem.modelmappers.CommentMapper;
import com.forumsystem.modelmappers.CommentResponseMapper;
import com.forumsystem.modelmappers.PostResponseMapper;
import com.forumsystem.models.*;
import com.forumsystem.models.modeldto.CommentDto;
import com.forumsystem.models.modeldto.CommentResponseDto;
import com.forumsystem.models.modeldto.PostDto;
import com.forumsystem.models.modeldto.PostResponseDto;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import com.forumsystem.modelmappers.PostMapper;
import com.forumsystem.services.contracts.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.forumsystem.modelhelpers.SwaggerConstantHelper.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final AuthenticationHelper authHelper;
    private final PostResponseMapper postResponseMapper;
    private final CommentResponseMapper commentResponseMapper;

    @Autowired
    public PostController(PostService postService,
                          PostMapper postMapper,
                          CommentMapper commentMapper,
                          AuthenticationHelper authHelper,
                          PostResponseMapper postResponseMapper, CommentResponseMapper commentResponseMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.authHelper = authHelper;
        this.postResponseMapper = postResponseMapper;
        this.commentResponseMapper = commentResponseMapper;
    }

    @Operation(summary = GET_POSTS_SUMMARY, description = GET_POSTS_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping()
    public List<PostResponseDto> getAllPosts(
            @RequestHeader HttpHeaders headers,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer likes,
            @RequestParam(required = false) Integer dislikes,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        PostModelFilterOptions postFilter = new PostModelFilterOptions(
                title, likes, dislikes, tagName, sortBy, sortOrder);
        try {
            User user = authHelper.tryGetUser(headers);
            List<Post> postList = postService.getAll(user, postFilter);
            return postResponseMapper.convertToDTO(postList);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/mostCommented")
    public List<PostResponseDto> getTopTenCommentedPosts(){
        List<Post> postList = postService.getTopTenCommentedPosts();
        return postResponseMapper.convertToDTO(postList);
    }

    @Operation(summary = GET_POST_BY_ID_SUMMARY, description = GET_POST_BY_ID_DESCRIPTION +
            ONLY_BY_LOGGED_USERS)
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{id}")
    public PostResponseDto getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authHelper.tryGetUser(headers);
        try {
            Post post = postService.getById(user, id);
            return postResponseMapper.convertToDTO(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = CREATE_POST_SUMMARY, description = CREATE_POST_DESCRIPTION +
            ONLY_BY_LOGGED_USERS)
    @SecurityRequirement(name = AUTHORIZATION)
    @PostMapping()
    public PostResponseDto create(@RequestHeader HttpHeaders headers, @RequestBody @Valid PostDto postDto) {
        try {
            Post post = postMapper.fromDto(postDto);
            User user = authHelper.tryGetUser(headers);
            postService.create(user, post);
            return postResponseMapper.convertToDTO(post);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Operation(summary = CREATE_COMMENT_SUMMARY, description = CREATE_COMMENT_DESCRIPTION +
            ONLY_BY_LOGGED_USERS)
    @SecurityRequirement(name = AUTHORIZATION)
    @PostMapping("/{post_id}/comments")
    public CommentResponseDto createComment(@RequestHeader HttpHeaders headers,
                                            @RequestBody @Valid CommentDto commentDto,
                                            @PathVariable int post_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            Comment comment = commentMapper.fromDto(commentDto);
            postService.createComment(user, comment, post_id);
            return commentResponseMapper.convertToDTO(comment);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Operation(summary = UPDATE_POST_SUMMARY, description = UPDATE_POST_DESCRIPTION +
            ONLY_BY_ADMINS_AND_CREATOR + POST)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/{id}")
    public PostResponseDto update(@RequestHeader HttpHeaders headers,
                                  @RequestBody @Valid PostDto postDto,
                                  @PathVariable int id) {
        try {
            Post post = postMapper.fromDto(postDto, id);
            User user = authHelper.tryGetUser(headers);
            postService.updatePost(user, post);
            return postResponseMapper.convertToDTO(post);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = UPDATE_COMMENT_SUMMARY, description = UPDATE_COMMENT_DESCRIPTION +
            ONLY_BY_ADMINS_AND_CREATOR + COMMENT)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("{post_id}/comments/{comment_id}")
    public CommentResponseDto updateComment(@RequestHeader HttpHeaders headers,
                                 @RequestBody @Valid CommentDto commentDto,
                                 @PathVariable int post_id,
                                 @PathVariable int comment_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            Comment comment = commentMapper.fromDto(commentDto, comment_id);
            postService.updateComment(user, comment, post_id);
            return commentResponseMapper.convertToDTO(comment);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = LIKE_POST_SUMMARY, description = LIKE_POST_DESCRIPTION +
            ONLY_BY_LOGGED_USERS)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("{post_id}/like")
    public void likePost(@RequestHeader HttpHeaders headers,
                         @PathVariable int post_id) {
        User user = authHelper.tryGetUser(headers);
        Post post = postService.getById(user, post_id);
        postService.likePost(post, user);
    }

    @Operation(summary = DISLIKE_POST_SUMMARY, description = DISLIKE_POST_DESCRIPTION +
            ONLY_BY_LOGGED_USERS)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("{post_id}/dislike")
    public void dislikePost(@RequestHeader HttpHeaders headers,
                            @PathVariable int post_id) {
        User user = authHelper.tryGetUser(headers);
        Post post = postService.getById(user, post_id);
        postService.dislikePost(post, user);
    }

    @Operation(summary = DELETE_POST_SUMMARY, description = DELETE_POST_DESCRIPTION +
            ONLY_BY_ADMINS_AND_CREATOR + POST)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/post/{post_id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int post_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            postService.delete(user, post_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = DELETE_COMMENT_SUMMARY, description = DELETE_COMMENT_DESCRIPTION +
            ONLY_BY_ADMINS_AND_CREATOR + POST)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/{post_id}/comment/{comment_id}")
    public void deleteComment(@RequestHeader HttpHeaders headers,
                              @PathVariable int post_id,
                              @PathVariable int comment_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            postService.deleteComment(user, post_id, comment_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
