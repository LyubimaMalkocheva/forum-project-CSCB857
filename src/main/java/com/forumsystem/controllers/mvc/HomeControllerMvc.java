package com.forumsystem.controllers.mvc;

import com.forumsystem.modelmappers.PostResponseMapper;
import com.forumsystem.models.Post;
import com.forumsystem.services.contracts.PostService;
import com.forumsystem.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeControllerMvc {
    private final UserService userService;
    private final PostService postService;
    private final PostResponseMapper postResponseMapper;

    @Autowired
    public HomeControllerMvc(UserService userService, PostService postService, PostResponseMapper postResponseMapper) {
        this.userService = userService;
        this.postService = postService;
        this.postResponseMapper = postResponseMapper;
    }

    @GetMapping
    public String showHomePage(Model model) {

        long postCount = postService.getPostCount();


        long userCount = userService.getCountUsers();


        List<Post> postList = postService.getTopTenCommentedPosts();


        List<Post> postNewList = postService.getTenNewestPosts();

        model.addAttribute("postCount", postCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("tenNewestPosts", postNewList);
        model.addAttribute("postCommentList", postList);

        return "HomePageView";
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

}
