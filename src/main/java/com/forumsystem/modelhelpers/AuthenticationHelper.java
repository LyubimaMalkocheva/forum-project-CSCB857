package com.forumsystem.modelhelpers;

import com.forumsystem.models.User;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.services.contracts.UserService;
import com.forumsystem.еxceptions.AuthenticationFailureException;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import  org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

import static com.forumsystem.modelhelpers.ModelConstantHelper.*;

@Component
public class AuthenticationHelper {

    private final UserService service;
    private final UserRepository repository;

    @Autowired
    public AuthenticationHelper(UserService service, UserRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    THE_REQUEST_RESOURCE_REQUIRES_AUTHENTICATION);
        }
        try {
            String authorizationHeader = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            if(authorizationHeader.contains("Basic ") &&
                    Base64.isBase64(authorizationHeader.substring("Basic ".length()))){
                authorizationHeader = new String(Base64.decodeBase64(authorizationHeader
                        .substring("Basic ".length())), "UTF-8").replace(":", " ");
            }
            String username = getUsername(authorizationHeader);
            String password = getPassword(authorizationHeader);

            User user = service.getUserByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_AUTHENTICATION);
            }

            return user;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_AUTHENTICATION);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUsername(String authorizationHeader) {
        int firstSpaceIndex = authorizationHeader.indexOf(" ");
        if (firstSpaceIndex == -1) {
            throw new UnauthorizedOperationException(INVALID_AUTHENTICATION);
        }
        return authorizationHeader.substring(0, firstSpaceIndex);
    }

    private String getPassword(String authorizationHeader) {
        int firstSpaceIndex = authorizationHeader.indexOf(" ");
        if (firstSpaceIndex == -1) {
            throw new UnauthorizedOperationException(INVALID_AUTHENTICATION);
        }
        return authorizationHeader.substring(firstSpaceIndex + 1);
    }

    public User verifyAuthentication(String username, String password){
        try {
            User user = service.getUserByUsername(username);
            if (!user.getPassword().equals(password)){
                throw new AuthenticationFailureException(WRONG_USERNAME_OR_PASSWORD);
            }
            return user;
        }catch (EntityNotFoundException e){
            throw new AuthenticationFailureException(WRONG_USERNAME_OR_PASSWORD);
        }
    }

    public void verifyUserAccess(int id, User loggedUser){
        service.checkPermissions(repository.get(id), loggedUser);
    }

    public User tryGetUser(HttpSession session) {
        String currentUsername = (String) session.getAttribute("currentUser");

        if (currentUsername == null) {
            throw new AuthenticationFailureException("Invalid authentication.");
        }

        return service.getUserByUsername(currentUsername);
    }
}
