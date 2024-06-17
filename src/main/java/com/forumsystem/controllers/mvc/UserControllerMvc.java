package com.forumsystem.controllers.mvc;

import com.forumsystem.modelhelpers.AuthenticationHelper;
import com.forumsystem.modelmappers.PostResponseMapper;
import com.forumsystem.modelmappers.UserMapper;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.PostResponseDto;
import com.forumsystem.models.modeldto.AdminDto;
import com.forumsystem.models.modeldto.ProfilePictureDto;
import com.forumsystem.models.modeldto.UpdateUserPasswordDto;
import com.forumsystem.models.modeldto.UserProfileUpdateDto;
import com.forumsystem.services.contracts.UserService;
import com.forumsystem.еxceptions.AuthenticationFailureException;
import com.forumsystem.еxceptions.DuplicateEntityException;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@ControllerAdvice
@RequestMapping("/users")
public class UserControllerMvc {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    private final PostResponseMapper postResponseMapper;

    @Autowired
    public UserControllerMvc(UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper, PostResponseMapper postResponseMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
        this.postResponseMapper = postResponseMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/userProfile")
    public String showCurrentUserProfile(Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
            UserProfileUpdateDto userToUpdate = userMapper.toUpdateProfileDto(currentUser);
            ProfilePictureDto pictureDto = userMapper.toProfilePictureDto(currentUser);
            model.addAttribute("userId", currentUser.getUserId());
            model.addAttribute("loggedUser", userToUpdate);
            model.addAttribute("password", new UpdateUserPasswordDto());
            model.addAttribute("profilePicture", pictureDto);
            model.addAttribute("admin", userService.checkIfAdmin(currentUser.getUserId()));
            model.addAttribute("adminPhone", new AdminDto(userService.getAdminPhoneNumber(currentUser)));
            return "UserProfileView";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{id}")
    public String showUserProfile(@PathVariable int id,
                                  Model model,
                                  HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetUser(session);
            User userToUpdate = userService.get(id, currentUser);
            UserProfileUpdateDto userToUpdateDto = userMapper.toUpdateProfileDto(userToUpdate);
            ProfilePictureDto pictureDto = userMapper.toProfilePictureDto(userToUpdate);
            model.addAttribute("userId", id);
            model.addAttribute("loggedUser", userToUpdateDto);
            model.addAttribute("password", new UpdateUserPasswordDto());
            model.addAttribute("profilePicture", pictureDto);
            model.addAttribute("admin", userService.checkIfAdmin(userToUpdate.getUserId()));
            model.addAttribute("adminPhone", new AdminDto(userService.getAdminPhoneNumber(userToUpdate)));
            return "UserProfileView";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable int id,
                             @Valid @ModelAttribute("loggedUser") UserProfileUpdateDto updatedUser,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model) {


        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            authenticationHelper.verifyUserAccess(id, loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
        try {
            User userToUpdate = userService.get(id, loggedUser);
            ProfilePictureDto pictureDto = userMapper.toProfilePictureDto(userToUpdate);

            if (bindingResult.hasErrors()) {
                model.addAttribute("loggedUser", updatedUser);
                model.addAttribute("userId", id);
                model.addAttribute("password", new UpdateUserPasswordDto());
                model.addAttribute("adminPhone", new AdminDto(userService.getAdminPhoneNumber(userToUpdate)));
                model.addAttribute("profilePicture", pictureDto);
                model.addAttribute("admin", userService.checkIfAdmin(loggedUser.getUserId()));
                return "UserProfileView";
            }

            User userWhoWillBeUpdated = userMapper.fromDto(updatedUser);
            userService.update(userWhoWillBeUpdated, loggedUser);
            return returnUserToRespectiveUserProfileView(loggedUser, id);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return returnUserToRespectiveUserProfileView(loggedUser, id);
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }
    }

    @PostMapping("/{id}/password")
    public String updateUserPassword(@PathVariable int id,
                                     @Valid @ModelAttribute("password") UpdateUserPasswordDto passwordDto,
                                     BindingResult bindingResult,
                                     HttpSession session,
                                     Model model) {


        User loggedUser;

        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            authenticationHelper.verifyUserAccess(id, loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }


        try {
            UserProfileUpdateDto userProfileUpdate = fillUserData(id, loggedUser);
            User userToUpdate = userService.get(id, loggedUser);
            ProfilePictureDto pictureDto = userMapper.toProfilePictureDto(userToUpdate);

            if (bindingResult.hasErrors()) {
                fillModelAttributesForPassword(model, passwordDto, userProfileUpdate,
                        id, pictureDto, userToUpdate, loggedUser);

                return "UserProfileView";
            }

            if (!userService.confirmIfPasswordsMatch(id, passwordDto)) {
                bindingResult.rejectValue("currentPassword",
                        "password_error", "Wrong Password");

                fillModelAttributesForPassword(model, passwordDto, userProfileUpdate,
                        id, pictureDto, userToUpdate, loggedUser);
                return "UserProfileView";
            }
            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                bindingResult.rejectValue("confirmNewPassword",
                        "password_error", "Passwords mismatch.");

                fillModelAttributesForPassword(model, passwordDto, userProfileUpdate,
                        id, pictureDto, userToUpdate, loggedUser);
                return "UserProfileView";
            }

            User userWhosePasswordWillBeUpdated = userMapper.fromDto(id, loggedUser, passwordDto);
            userService.update(userWhosePasswordWillBeUpdated, loggedUser);

        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        }

        return returnUserToRespectiveUserProfileView(loggedUser, id);
    }

    @PostMapping("/{id}/admin-rights")
    public String giveUserAdminRights(@PathVariable int id,
                                      HttpSession session,
                                      Model model) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            authenticationHelper.verifyUserAccess(id, loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "UnauthorizedView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }

        try {
            User user = userService.get(id, loggedUser);
            userService.giveUserAdminRights(user, loggedUser);
            return "redirect:/admin/users";
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


    @GetMapping("/{username}/posts")
    public String showUserPosts(@PathVariable String username,
                                HttpSession session,
                                Model model) {
        try {
            authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        try {
            List<Post> userPosts = userService.getUserPosts(username);
            List<PostResponseDto> outputPosts = postResponseMapper.convertToDTO(userPosts);

            model.addAttribute("username", username);
            model.addAttribute("userPosts", outputPosts);

            return "UserPostsView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id, Model model, HttpSession session) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            authenticationHelper.verifyUserAccess(id, loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            userService.delete(id, loggedUser);
            return "redirect:/admin/users";
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

    @PostMapping("/{id}/block")
    public String block(@PathVariable int id,
                        HttpSession session,
                        Model model) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            authenticationHelper.verifyUserAccess(id, loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }

        try {
            userService.blockUser(id, loggedUser);
            return "redirect:/admin/users";
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

    @PostMapping("/{id}/unblock")
    public String unblock(@PathVariable int id,
                          HttpSession session,
                          Model model) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            authenticationHelper.verifyUserAccess(id, loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "NotFoundView";
        }

        try {
            userService.unblockUser(id, loggedUser);
            return "redirect:/admin/users";
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

    @PostMapping("/{id}/profilePicture")
    public String uploadProfilePicture(@PathVariable int id,
                                       HttpSession session,
                                       @ModelAttribute(name = "profilePicture") ProfilePictureDto pictureDto,
                                       @RequestParam("fileImage") MultipartFile multipartFile,
                                       Model model) throws IOException {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            authenticationHelper.verifyUserAccess(id, loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        pictureDto.setPictureName(fileName);
        User userWithSaveProfilePic = userMapper.fromDto(pictureDto, id, loggedUser);

        userService.update(userWithSaveProfilePic, loggedUser);

        String uploadDir = "./user-profilePictures/" + userWithSaveProfilePic.getUserId();

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            model.addAttribute("profilePicture", pictureDto);
            model.addAttribute("userId", id);
            model.addAttribute("loggedUser", userWithSaveProfilePic);
            model.addAttribute("password", new UpdateUserPasswordDto());
            model.addAttribute("profilePicture", pictureDto);
            model.addAttribute("admin", userService.checkIfAdmin(loggedUser.getUserId()));
            model.addAttribute("adminPhone", new AdminDto(userService.getAdminPhoneNumber(userWithSaveProfilePic)));
        }

        return returnUserToRespectiveUserProfileView(loggedUser, id);
    }

    @PostMapping("/{id}/phoneNumber")
    public String updatePhoneNumber(@PathVariable int id,
                                    HttpSession session,
                                    @Valid @ModelAttribute("adminPhone") AdminDto adminDto,
                                    BindingResult bindingResult,
                                    Model model) {

        User loggedUser;

        try {
            loggedUser = authenticationHelper.tryGetUser(session);
            userService.checkIfAdmin(loggedUser);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }

        UserProfileUpdateDto userProfileUpdate = fillUserData(id, loggedUser);
        User userToUpdate = userService.get(id, loggedUser);
        ProfilePictureDto pictureDto = userMapper.toProfilePictureDto(userToUpdate);

        if (bindingResult.hasErrors()) {
            model.addAttribute("password", new UpdateUserPasswordDto());
            model.addAttribute("loggedUser", userProfileUpdate);
            model.addAttribute("userId", id);
            model.addAttribute("adminPhone", adminDto);
            model.addAttribute("profilePicture", pictureDto);
            model.addAttribute("admin", userService.checkIfAdmin(loggedUser.getUserId()));
            return "UserProfileView";
        }

        userService.updatePhoneNumber(adminDto, userToUpdate);
        return returnUserToRespectiveUserProfileView(loggedUser, id);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleImageUploadError(RedirectAttributes redirectAttributes,
                                         HttpSession session) {
        User loggedUser = authenticationHelper.tryGetUser(session);


        redirectAttributes.addFlashAttribute("error",
                "You cannot upload images above " + maxFileSize);

        return returnUserToRespectiveUserProfileView(loggedUser, loggedUser.getUserId());
    }


    private String returnUserToRespectiveUserProfileView(User user, int id) {
        if (userService.checkIfAdmin(user.getUserId())) {
            return "redirect:/users/" + id;
        }
        return "redirect:/users/userProfile";
    }

    private UserProfileUpdateDto fillUserData(int userId, User user) {
        User userToConvert = userService.get(userId, user);
        return userMapper.toUpdateProfileDto(userToConvert);
    }

    private void fillModelAttributesForPassword(Model model,
                                                UpdateUserPasswordDto passwordDto,
                                                UserProfileUpdateDto userProfileUpdate,
                                                int id, ProfilePictureDto pictureDto,
                                                User userToUpdate,
                                                User loggedUser) {

        model.addAttribute("password", passwordDto);
        model.addAttribute("loggedUser", userProfileUpdate);
        model.addAttribute("userId", id);
        model.addAttribute("adminPhone", new AdminDto(userService.getAdminPhoneNumber(userToUpdate)));
        model.addAttribute("profilePicture", pictureDto);
        model.addAttribute("admin", userService.checkIfAdmin(loggedUser.getUserId()));
    }
}
