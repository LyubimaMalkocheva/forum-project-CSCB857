package com.forumsystem.controllers.mvc;

import com.forumsystem.modelhelpers.AuthenticationHelper;
import com.forumsystem.modelhelpers.PostModelFilterOptions;
import com.forumsystem.modelmappers.CommentMapper;
import com.forumsystem.modelmappers.PostMapper;
import com.forumsystem.modelmappers.PostResponseMapper;
import com.forumsystem.models.*;
import com.forumsystem.models.modeldto.*;
import com.forumsystem.services.contracts.CommentService;
import com.forumsystem.services.contracts.PostService;
import com.forumsystem.services.contracts.UserService;
import com.forumsystem.еxceptions.AuthenticationFailureException;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostMvcController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final AuthenticationHelper authHelper;
    private final PostResponseMapper postResponseMapper;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public PostMvcController(PostService postService,
                             PostMapper postMapper,
                             CommentService commentService,
                             CommentMapper commentMapper,
                             AuthenticationHelper authHelper,
                             PostResponseMapper postResponseMapper,
                             UserService userService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.authHelper = authHelper;
        this.postResponseMapper = postResponseMapper;
        this.userService = userService;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }
    @GetMapping()
    public String ShowAllPosts(
            Model model,
            @ModelAttribute("postFilterOptions") PostModelFilterDto postFilterDto,
            HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        PostModelFilterOptions postFilter = new PostModelFilterOptions(
                postFilterDto.getTitle(),
                postFilterDto.getLikes(),
                postFilterDto.getDislikes(),
                postFilterDto.getTagName(),
                postFilterDto.getSortBy(),
                postFilterDto.getSortOrder());

        List<Post> posts = postService.getAll(user, postFilter);
        List<PostResponseDto> outputPosts = postResponseMapper.convertToDTO(posts);
        model.addAttribute("posts", outputPosts);
        model.addAttribute("postFilterOptions", postFilterDto);
        return "PostsView";
    }


    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable int id,
                                 Model model,
                                 HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            PostResponseDto post = postResponseMapper
                    .convertToDTO(postService.getById(user, id));
            List<CommentResponseDto> postComments = commentService.getAll(id);

            model.addAttribute("postId", id);
            model.addAttribute("post", post);
            model.addAttribute("postToCompare", postService.getById(user, id));
            model.addAttribute("postComments", postComments);
            model.addAttribute("userPosts", userService.getUserPosts(user.getUsername()));
            model.addAttribute("user", user);
            session.setAttribute("isAdmin", userService.checkIfAdmin(user.getUserId()));

            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @GetMapping("/new")
    public String showCreatePostPage(Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        model.addAttribute("newPost", new PostDto());
        return "CreateNewPostView";
    }


    @PostMapping("/new")
    public String createPost(@ModelAttribute("newPost") @Valid PostDto postDto,
                             BindingResult errors,
                             HttpSession session,
                             Model model) {

        if (errors.hasErrors()) {

            return "CreateNewPostView";
        }

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }


        try {
            Post post = postMapper.fromDto(postDto);
            postService.create(user, post);
            return "redirect:/posts";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditPostPage(@PathVariable int id,
                                   Model model,
                                   HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        Post post = postService.getById(user, id);
        PostResponseDtoMvc outputPost = postResponseMapper.convertToDtoUpdate(post);
        model.addAttribute("postId", id);
        model.addAttribute("postMvc", outputPost);
        return "PostUpdateView";
    }

    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable int id,
                             @Valid @ModelAttribute("postMvc") PostDtoMvc postDtoMvc,
                             BindingResult errors,
                             HttpSession session,
                             Model model) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        if (errors.hasErrors()) {
            model.addAttribute("postId", id);
            model.addAttribute("postMvc", postDtoMvc);
            return "PostUpdateView";
        }
        try {
            Post newPost = postMapper.fromDto(postDtoMvc, id);
            postService.updatePost(user, newPost);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable int id, Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            postService.delete(user, id);

            if (userService.checkIfAdmin(user.getUserId())) {
                return "redirect:/admin/posts";
            }
            return "redirect:/posts";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @GetMapping("/{id}/like")
    String likePost(@PathVariable int id, Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            Post post = postService.getById(user, id);
            postService.likePost(post, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{id}/dislike")
    public String dislikePost(@PathVariable int id, Model model, HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            Post post = postService.getById(user, id);
            postService.dislikePost(post, user);
            return "redirect:/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{post_id}/newComment")
    public String showCreateCommentPage(@PathVariable int post_id,
                                        Model model,
                                        HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            model.addAttribute("comment", new CommentDto());
            model.addAttribute("post", postService.getById(user, post_id));
            return "CreatePostCommentView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{post_id}/newComment")
    public String createPostComment(@PathVariable int post_id,
                                    @Valid @ModelAttribute("comment") CommentDto commentDto,
                                    BindingResult errors,
                                    Model model,
                                    HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        if (errors.hasErrors()) {
            model.addAttribute("post", postService.getById(user, post_id));
            return "CreatePostCommentView";
        }

        try {
            Comment comment = commentMapper.fromDto(commentDto);
            postService.createComment(user, comment, post_id);
            return "redirect:/posts/" + post_id;

        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @GetMapping("/{post_id}/comment/{comment_id}/update")
    public String showEditPostCommentPage(@PathVariable int post_id,
                                          @PathVariable int comment_id,
                                          Model model,
                                          HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            postService.getById(user, post_id);
            Comment comment = commentService.getById(comment_id);
            CommentDto commentDto = commentMapper.toDto(comment);
            model.addAttribute("commentId", comment_id);
            model.addAttribute("comment", commentDto);
            model.addAttribute("post", postService.getById(user, post_id));
            return "EditPostCommentView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{post_id}/comment/{comment_id}/update")
    public String updatePostComment(@PathVariable int post_id,
                                    @PathVariable int comment_id,
                                    @Valid @ModelAttribute("comment") CommentDto commentDto,
                                    BindingResult errors,
                                    Model model,
                                    HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        if (errors.hasErrors()) {
            model.addAttribute("commentId", comment_id);
            model.addAttribute("comment", commentDto);
            model.addAttribute("post", postService.getById(user, post_id));
            return "EditPostCommentView";
        }

        try {
            Comment newComment = commentMapper.fromDto(commentDto, comment_id);
            postService.updateComment(user, newComment, post_id);
            return "redirect:/posts/" + post_id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @GetMapping("/{post_id}/comment/{comment_id}/delete")
    public String deletePostComment(@PathVariable int post_id,
                                    @PathVariable int comment_id,
                                    Model model,
                                    HttpSession session) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            postService.deleteComment(user, post_id, comment_id);
            return "redirect:/posts/" + post_id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }
}
