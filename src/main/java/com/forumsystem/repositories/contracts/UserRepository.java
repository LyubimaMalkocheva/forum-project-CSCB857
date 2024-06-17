package com.forumsystem.repositories.contracts;

import com.forumsystem.modelhelpers.UserModelFilterOptions;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll(UserModelFilterOptions userFilter);

    User get(int id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    void create(User user);

    User update(User user);

    void delete(int id);

    List<Post> getUserPosts(String username);

    void blockUser(String username);

    void unblockUser(String username);

    boolean checkIfAdmin(int id);
    String getAdminPhoneNumber(int userId);

    long getCountUsers();

    boolean isEmailExists(User user);

    void giveUserAdminRights(User user);

    void updatePhoneNumber(String phoneNumber, int userId);
}
