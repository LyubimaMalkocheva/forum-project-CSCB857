package com.forumsystem.controllers.rest;

import com.forumsystem.modelhelpers.AuthenticationHelper;
import com.forumsystem.modelhelpers.UserModelFilterOptions;
import com.forumsystem.modelmappers.UserMapper;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.UserDto;
import com.forumsystem.services.contracts.UserService;
import com.forumsystem.еxceptions.DuplicateEntityException;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.forumsystem.modelhelpers.ModelConstantHelper.UNAUTHORIZED_TO_BROWSE_USER_INFORMATION;
import static com.forumsystem.modelhelpers.SwaggerConstantHelper.*;

;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    private void tryAuthenticateUser(int id, HttpHeaders headers) {
        User user = authenticationHelper.tryGetUser(headers);
        if (user.getUserId() != id) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    UNAUTHORIZED_TO_BROWSE_USER_INFORMATION);
        }
    }

    @Operation(summary = GET_ALL_USERS_SUMMARY, description = GET_ALL_USERS_DESCRIPTION + ONLY_BY_ADMINS )
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping()
    public List<User> getAll(@RequestHeader HttpHeaders headers,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String firstName,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(required = false) String sortOrder) {
        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                username, email, firstName, sortBy, sortOrder);
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return userService.getAll(user, userFilter);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    UNAUTHORIZED_TO_BROWSE_USER_INFORMATION);
        }
    }

    @Operation(summary = COUNT_USERS_SUMMARY, description = COUNT_USERS_DESCRIPTION)
    @GetMapping("/count")
    public long countUsers() {
        return userService.getCountUsers();
    }


    @Operation(summary = GET_USER_BY_ID_SUMMARY, description = GET_USER_BY_ID_DESCRIPTION +
             ONLY_BY_ADMINS_AND_CREATOR + PROFILE)
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return userService.get(id, user);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    UNAUTHORIZED_TO_BROWSE_USER_INFORMATION);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }
    @PostMapping("/{id}/admin-rights")
    public User giveUserAdminRights(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUser(headers);
            User toBeAdmin = userService.get(id, loggedUser);
            userService.giveUserAdminRights(toBeAdmin, loggedUser);
            return toBeAdmin;
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    UNAUTHORIZED_TO_BROWSE_USER_INFORMATION);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    @Operation(summary = GET_USER_POSTS_SUMMARY, description = GET_USER_POSTS_DESCRIPTION +
             ONLY_BY_LOGGED_USERS)
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{username}/posts")
    public List<Post> getUserPosts(@PathVariable String username, @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
            return userService.getUserPosts(username);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    UNAUTHORIZED_TO_BROWSE_USER_INFORMATION);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    @Operation(summary = CREATE_USER_SUMMARY, description = CREATE_USER_DESCRIPTION)
    @PostMapping
    public User create(@Valid @RequestBody UserDto userDto){
        try {
            User user = userMapper.fromDto(userDto);
            userService.create(user);
            return user;
        }  catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Operation(summary = UPDATE_USER_SUMMARY, description = UPDATE_USER_DESCRIPTION +
            ONLY_BY_ADMINS_AND_CREATOR + PROFILE)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/{id}")
    public User update(@PathVariable int id, @Valid @RequestBody UserDto userDto,
                       @RequestHeader HttpHeaders headers){
        try {
            User loggedUser = authenticationHelper.tryGetUser(headers);
            User userToUpdate = userMapper.fromDto(userDto);
            return userService.update(userToUpdate, loggedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

    }

    @Operation(summary = DELETE_USER_SUMMARY, description = DELETE_USER_DESCRIPTION +
            ONLY_BY_ADMINS_AND_CREATOR + PROFILE)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/delete/{id}")
    public void delete(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Operation(summary = BLOCK_USER_SUMMARY, description = BLOCK_USER_DESCRIPTION +
            ONLY_BY_ADMINS)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/{id}/block")
    public void block(@PathVariable int id,
                       @RequestHeader HttpHeaders headers){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.blockUser(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Operation(summary = UNBLOCK_USER_SUMMARY, description = UNBLOCK_USER_DESCRIPTION +
            ONLY_BY_ADMINS)
    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/{id}/unblock")
    public void unblock(@PathVariable int id,
                       @RequestHeader HttpHeaders headers){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.unblockUser(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
