package com.forumsystem.services.contracts;

import com.forumsystem.modelhelpers.UserModelFilterOptions;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.AdminDto;
import com.forumsystem.models.modeldto.UpdateUserPasswordDto;

import java.util.List;

public interface UserService {

    List<User> getAll(User user, UserModelFilterOptions userFilter);

    User get(int id, User user);

    User getUserByUsername(String username);

    void create(User user);

    User update(User userToUpdate, User loggedUser);

    void delete(int id, User user);

    List<Post> getUserPosts(String username);

    void blockUser(int id, User user);

    void unblockUser(int id, User user);

    long getCountUsers();

    void giveUserAdminRights(User user, User loggedUser);

    void checkPermissions(User userToUpdate, User loggedUser);

    /**
     * Throws UnauthorizedOperationException if the given User obj is not an admin
     * or in other words if the result boolean is false.
     */
    boolean checkIfAdmin(User user);

    /**
     * Returns either true or false depending on whether the user with the given ID is an admin or not.
     * Does NOT throw any kind of exception.
     */
    boolean checkIfAdmin(int id);

    String getAdminPhoneNumber(User user);

    boolean confirmIfPasswordsMatch(int id, UpdateUserPasswordDto passwordDto);

    void updatePhoneNumber(AdminDto adminDto, User user);
}
