package com.forumsystem.services;

import com.forumsystem.modelhelpers.UserModelFilterOptions;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.AdminDto;
import com.forumsystem.models.modeldto.UpdateUserPasswordDto;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.services.contracts.UserService;
import com.forumsystem.еxceptions.DuplicateEntityException;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.forumsystem.modelhelpers.ModelConstantHelper.PERMISSIONS_ERROR;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> getAll(User user, UserModelFilterOptions userFilter) {
        checkIfAdmin(user);
        return repository.getAll(userFilter);
    }

    @Override
    public User get(int id, User user) {
        checkPermissions(repository.get(id), user);
        return repository.get(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return repository.getUserByUsername(username);
    }

    @Override
    public void create(User user) {
        boolean duplicateUserNameExists = true;
        boolean duplicateEmailNameExists = true;

        try {
            repository.getUserByUsername(user.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateUserNameExists = false;
        }
        if (duplicateUserNameExists) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        try {
            repository.getUserByEmail(user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateEmailNameExists = false;
        }
        if (duplicateEmailNameExists) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
        repository.create(user);
    }

    @Override
    public User update(User userToUpdate, User loggedUser) {

        checkPermissions(userToUpdate, loggedUser);
        if (repository.isEmailExists(userToUpdate)) {
            throw new DuplicateEntityException("User", "email", loggedUser.getEmail());
        }
        return repository.update(userToUpdate);
    }

    @Override
    public void delete(int id, User user) {
        checkPermissions(repository.get(id), user);
        repository.delete(id);

    }

    @Override
    public List<Post> getUserPosts(String username) {
        try{
            return repository.getUserPosts(username);
        } catch (EntityNotFoundException e){
            return new ArrayList<>();
        }
    }

    @Override
    public void blockUser(int id, User user) {
        User blockUser = repository.get(id);
        checkIfAdmin(user);
        repository.blockUser(blockUser.getUsername());
    }

    @Override
    public void unblockUser(int id, User user) {
        User unblockUser = repository.get(id);
        checkIfAdmin(user);
        repository.unblockUser(unblockUser.getUsername());
    }

    @Override
    public long getCountUsers() {
        return repository.getCountUsers();
    }

    @Override
    public void giveUserAdminRights(User user, User loggedUser) {
        checkIfAdmin(loggedUser);

        if (user.isBlocked()) {
            repository.unblockUser(user.getUsername());
        }
        repository.giveUserAdminRights(user);
    }

    public void checkPermissions(User userToUpdate, User loggedUser) {
        if (!repository.checkIfAdmin(loggedUser.getUserId()) && userToUpdate.getUserId() != loggedUser.getUserId()) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
    }
    @Override
    public boolean confirmIfPasswordsMatch(int id, UpdateUserPasswordDto passwordDto) {
        User userWhosePasswordMayBeChanged = repository.get(id);
        return passwordDto.getCurrentPassword()
                .equals(userWhosePasswordMayBeChanged.getPassword());
    }

    @Override
    public void updatePhoneNumber(AdminDto adminDto, User user) {
        repository.updatePhoneNumber(adminDto.getPhoneNumber(), user.getUserId());
    }

    @Override
    public boolean checkIfAdmin(User loggedUser) {
        if (!repository.checkIfAdmin(loggedUser.getUserId())) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        } else {
            return true;
        }
    }

    @Override
    public boolean checkIfAdmin(int id) {
        return repository.checkIfAdmin(id);
    }

    @Override
    public String getAdminPhoneNumber(User user){
        try {
            return repository.getAdminPhoneNumber(user.getUserId());
        } catch (EntityNotFoundException e){
            return "0";
        }
    }


    private void checkUserIsBlocked(User loggedUser) {
        if (loggedUser.isBlocked()) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR);
        }
    }

}
