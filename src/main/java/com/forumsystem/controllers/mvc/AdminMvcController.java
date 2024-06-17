package com.forumsystem.controllers.mvc;

import com.forumsystem.modelhelpers.AuthenticationHelper;
import com.forumsystem.modelhelpers.PostModelFilterOptions;
import com.forumsystem.modelhelpers.UserModelFilterOptions;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.PostModelFilterDto;
import com.forumsystem.models.modeldto.UserModelFilterDto;
import com.forumsystem.services.contracts.PostService;
import com.forumsystem.services.contracts.UserService;
import com.forumsystem.еxceptions.AuthenticationFailureException;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.forumsystem.modelhelpers.ModelConstantHelper.UNAUTHORIZED;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private final AuthenticationHelper authHelper;
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public AdminMvcController(AuthenticationHelper authHelper,
                              UserService userService,
                              PostService postService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.postService = postService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping()
    public String showAdminDashboard(HttpSession session, Model model) {

        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            userService.checkIfAdmin(user);
            return "AdminPanelView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", UNAUTHORIZED);
            return "UnauthorizedView";
        }
    }

    @GetMapping("/posts")
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

        try {
            List<Post> posts = postService.getAllForAdmin(user, postFilter);
            model.addAttribute("postFilterOptions", postFilterDto);
            model.addAttribute("posts", posts);
            return "AdminPostsView";

        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", UNAUTHORIZED);
            return "UnauthorizedView";
        }

    }

    @GetMapping("/users")
    public String showAllUsers(Model model,
                               @ModelAttribute("userFilterOptions") UserModelFilterDto userFilterDto,
                               HttpSession session) {
        User user;
        try {
            user = authHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                userFilterDto.getUsername(),
                userFilterDto.getEmail(),
                userFilterDto.getFirstName(),
                userFilterDto.getSortBy(),
                userFilterDto.getSortOrder());

        try {
            userService.checkIfAdmin(user);
            List<User> users = userService.getAll(user, userFilter);
            model.addAttribute("userFilterOptions", userFilterDto);
            model.addAttribute("users", users);
            return "AdminUsersView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", UNAUTHORIZED);
            return "UnauthorizedView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }
}
