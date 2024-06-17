package com.forumsystem.services;

import com.forumsystem.modelhelpers.UserModelFilterOptions;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.AdminDto;
import com.forumsystem.models.modeldto.UpdateUserPasswordDto;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.еxceptions.DuplicateEntityException;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.forumsystem.Helpers.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAll_Should_ReturnUsers_ifUserIsAdmin() {
        //Arrange
        User adminUser = createMockAdminUser();

        UserModelFilterOptions userFilter = createMockUserFilterOptions();
        Mockito.when(userRepository.checkIfAdmin(adminUser.getUserId())).thenReturn(true);
        Mockito.when(userRepository.getAll(userFilter)).
                thenReturn(Arrays.asList(new User(), new User()));


        //Act
        List<User> result = userService.getAll(adminUser, userFilter);

        //Assert
        Mockito.verify(userRepository).getAll(userFilter);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getAll_Should_ThrowUnauthorizedOperationException_When_UserIsNotAdmin() {
        //Arrange
        User user = createMockUser();
        UserModelFilterOptions userFilter = createMockUserFilterOptions();

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.getAll(user, userFilter));
    }

    @Test
    void get_Should_ReturnUser_When_ValidArguments_And_UserIsAdmin() {

        //Arrange
        User adminUser = createMockAdminUser();
        User user = new User();
        user.setUserId(2);

        Mockito.when(userRepository.checkIfAdmin(adminUser.getUserId())).thenReturn(true);
        Mockito.when(userRepository.get(user.getUserId())).thenReturn(user);

        //Act
        User result = userService.get(user.getUserId(), adminUser);

        //Act & Assert
        Mockito.verify(userRepository, Mockito.times(2)).get(user.getUserId());
        Assertions.assertEquals(user, result);
    }

    @Test
    void get_Should_ReturnUser_When_ValidArguments_And_UserIsNotAdmin() {

        //Arrange
        User user = new User();
        user.setUserId(2);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);
        Mockito.when(userRepository.get(user.getUserId())).thenReturn(user);

        //Act
        User result = userService.get(user.getUserId(), user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(2)).get(user.getUserId());
        Assertions.assertEquals(user, result);
    }

    @Test
    void get_Should_Throw_UnauthorizedOperationException_When_ArgumentsInvalid() {

        //Arrange
        User user = new User();
        user.setUserId(2);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);
        Mockito.when(userRepository.get(3)).thenReturn(new User());

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.get(3, user));
    }

    @Test
    void getUserByUsername_Should_ReturnUser_When_ArgumentsValid() {
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.getUserByUsername(user.getUsername())).thenReturn(user);

        //Act
        User result = userService.getUserByUsername(user.getUsername());

        //Assert
        Mockito.verify(userRepository, Mockito.times(1)).getUserByUsername(user.getUsername());
        Assertions.assertEquals(user, result);
        Assertions.assertEquals("mockUsername", user.getUsername());
        Assertions.assertEquals(2, user.getUserId());
    }

    @Test
    void create_Should_CreateUser_When_SuchUserDoesNotExist() {
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.getUserByUsername(user.getUsername()))
                .thenThrow(new EntityNotFoundException("User", "name", user.getUsername()));
        Mockito.when(userRepository.getUserByEmail(user.getEmail()))
                .thenThrow(new EntityNotFoundException("User", "email", user.getEmail()));

        //Act & Assert
        userService.create(user);
        Mockito.verify(userRepository, Mockito.times(1))
                .create(Mockito.any(User.class));
    }


    @Test
    void create_Should_Throw_DuplicateEntityException_When_UserWithSameUsernameExists() {
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.getUserByUsername(user.getUsername())).thenReturn(user);

        //Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.create(user));

    }

    @Test
    void create_Should_Throw_DuplicateEntityException_When_UserWithSameEmailExists() {
        //Arrange
        User user = createMockUser();


        Mockito.when(userRepository.getUserByUsername(user.getUsername())).thenThrow(EntityNotFoundException.class);
        Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

        //Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.create(user));

    }

    @Test
    void update_Should_Update_User_When_ArgumentsValid() {
        //Arrange
        User user = createMockUser();
        User userToUpdate = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);
        Mockito.when(userRepository.isEmailExists(userToUpdate)).thenReturn(false);

        //Act & Assert
        userService.update(userToUpdate, user);
        Mockito.verify(userRepository, Mockito.times(1))
                .update(Mockito.any(User.class));


    }

    @Test
    void update_Should_Throw_DuplicateEntityException_When_EmailAlreadyExists() {
        User user = createMockUser();
        User userToUpdate = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);
        Mockito.when(userRepository.isEmailExists(userToUpdate)).thenReturn(true);

        //Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.update(userToUpdate, user));
    }

    @Test
    void update_Should_Throw_UnauthorizedOperationException_When_ArgumentsInvalid() {
        //Arrange
        User user = createMockUser();
        User userToUpdate = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.update(userToUpdate, user));
    }

    @Test
    void delete_Should_Delete_When_ArgumentsValid() {
        //Arrange
        User user = createMockUser();
        int id = 5;

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);

        //Act
        userService.delete(5, user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .delete(id);
    }

    @Test
    void delete_Should_Throw_UnauthorizedOperationException_When_Arguments_Invalid() {
        //Arrange
        User user = createMockUser();
        int id = 5;

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);
        Mockito.when(userRepository.get(5)).thenReturn(new User());

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.delete(id, user));
    }

    @Test
    void getUserPosts_Should_Return_UserPosts_When_UserHasPosts() {
        //Arrange
        String username = "username";

        Mockito.when(userRepository.getUserPosts(username)).thenReturn(Arrays.asList(new Post(), new Post()));

        //Act
        List<Post> result = userRepository.getUserPosts(username);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .getUserPosts(username);
        Assertions.assertEquals(2, result.size());

    }

    @Test
    void getUserPosts_Should_ReturnEmptyPostList_When_UserDoesNotHavePosts() {
        //Arrange
        String username = "username";

        Mockito.when(userRepository.getUserPosts(username))
                .thenThrow(EntityNotFoundException.class);

        //Act
        List<Post> result = userService.getUserPosts(username);

        //Assert
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void blockUser_Should_BlockUser_When_ArgumentsValid() {
        //Arrange
        User user = createMockUser();
        User userToBlock = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.get(userToBlock.getUserId())).thenReturn(userToBlock);
        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);

        //Act
        userService.blockUser(userToBlock.getUserId(), user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .blockUser(userToBlock.getUsername());
    }

    @Test
    void blockUser_Should_Throw_When_LoggedUserIsNotAdmin() {
        //Arrange
        User user = createMockUser();
        User userToBlock = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.get(userToBlock.getUserId())).thenReturn(userToBlock);
        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);

        //Act & Assert
        Mockito.verify(userRepository, Mockito.times(0))
                .blockUser(userToBlock.getUsername());
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.blockUser(userToBlock.getUserId(), user));
    }

    @Test
    void unblockUser_Should_UnblockUser_When_ArgumentsValid() {
        //Arrange
        User user = createMockUser();
        User userToBlock = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.get(userToBlock.getUserId())).thenReturn(userToBlock);
        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);

        //Act
        userService.unblockUser(userToBlock.getUserId(), user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .unblockUser(userToBlock.getUsername());
    }

    @Test
    void unblockUser_Should_Throw_When_LoggedUserIsNotAdmin() {
        //Arrange
        User user = createMockUser();
        User userToBlock = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.get(userToBlock.getUserId())).thenReturn(userToBlock);
        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);

        //Act & Assert
        Mockito.verify(userRepository, Mockito.times(0))
                .unblockUser(userToBlock.getUsername());
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.unblockUser(userToBlock.getUserId(), user));
    }

    @Test
    void getUserCount_Should_CallRepository() {
        //Arrange
        long userCount = 50;

        Mockito.when(userRepository.getCountUsers()).thenReturn(userCount);

        //Act
        userService.getCountUsers();

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .getCountUsers();
        Assertions.assertEquals(50, userService.getCountUsers());
    }

    @Test
    void giveUserAdminRights_Should_MakeUserAdmin_When_Arguments_Valid() {
        //Arrange
        User user = createMockUser();
        User userToMakeAdmin = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);
        //Act
        userService.giveUserAdminRights(userToMakeAdmin, user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .giveUserAdminRights(userToMakeAdmin);
    }

    @Test
    void giveUserAdminRights_Should_UnblockUser_If_UserWasPreviouslyBlocked() {
        //Arrange
        User user = createMockUser();
        User userToMakeAdmin = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                true, false);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);

        //Act
        userService.giveUserAdminRights(userToMakeAdmin, user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .unblockUser(userToMakeAdmin.getUsername());
    }

    @Test
    void giveUserAdminRights_Should_Throw_UnauthorizedOperationException_When_UserIsNotAdmin() {
        //Arrange
        User user = createMockUser();
        User userToMakeAdmin = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.giveUserAdminRights(userToMakeAdmin, user));
    }

    @Test
    void checkPermissions_Should_Throw_UnauthorizedOperationException_When_ArgumentsInvalid() {
        //Arrange
        User user = createMockUser();
        User userToUpdate = new User(3, "username", "firstName",
                "lastName", "email", "pass1234!",
                false, false);

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.checkPermissions(userToUpdate, user));
    }

    @Test
    void confirmIfPasswordsMatch_Should_ReturnTrue_When_PasswordsMatch() {
        User user = createMockUser();
        UpdateUserPasswordDto passwordDto = new UpdateUserPasswordDto();
        passwordDto.setCurrentPassword(user.getPassword());

        Mockito.when(userRepository.get(user.getUserId())).thenReturn(user);

        Assertions.assertTrue(userService.confirmIfPasswordsMatch(user.getUserId(), passwordDto));

    }

    @Test
    void confirmIfPasswordsMatch_Should_ReturnFalse_When_PasswordsMismatch() {
        User user = createMockUser();
        UpdateUserPasswordDto passwordDto = new UpdateUserPasswordDto();
        passwordDto.setCurrentPassword("wrongPassword");

        Mockito.when(userRepository.get(user.getUserId())).thenReturn(user);

        Assertions.assertFalse(userService.confirmIfPasswordsMatch(user.getUserId(), passwordDto));

    }

    @Test
    void updatePhoneNumber_Should_Call_Repository() {
        //Arrange
        AdminDto adminDto = new AdminDto("0898445555");
        User user = createMockUser();

        //Act
        userService.updatePhoneNumber(adminDto, user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .updatePhoneNumber(adminDto.getPhoneNumber(), user.getUserId());
    }

    @Test
    void checkIfAdmin_Should_ReturnTrue_When_UserIsAdmin() {
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);

        //Act & Assert
        Assertions.assertTrue(userService.checkIfAdmin(user));
    }

    @Test
    void checkIfAdmin_Should_Throw_UnauthorizedOperationException_When_UserIsNotAdmin() {
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);

        //Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.checkIfAdmin(user));
    }

    @Test
    void checkIfAdmin_Should_ReturnTrue_When_UserIdBelongsToAnAdmin() {
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);

        //Act & Assert
        Assertions.assertTrue(userService.checkIfAdmin(user.getUserId()));
    }

    @Test
    void checkIfAdmin_Should_ReturnFalse_When_UserIsNotAdmin() {
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);

        //Act & Assert
        Assertions.assertFalse(userService.checkIfAdmin(user.getUserId()));
    }

    @Test
    void getAdminPhone_Should_ReturnPhoneNumber_When_AdminExists(){
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.getAdminPhoneNumber(user.getUserId())).thenReturn("0898445555");

        //Act
        String phoneNumber = userService.getAdminPhoneNumber(user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .getAdminPhoneNumber(user.getUserId());
        Assertions.assertEquals("0898445555", userService.getAdminPhoneNumber(user));
    }

    @Test
    void getAdminPhone_Should_ReturnZero_When_AdminDoesNotExist(){
        //Arrange
        User user = createMockUser();

        Mockito.when(userRepository.getAdminPhoneNumber(user.getUserId()))
                .thenThrow(new EntityNotFoundException("admin", "id", String.valueOf(user.getUserId())));

        //Act
        String phoneNumber = userService.getAdminPhoneNumber(user);

        //Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .getAdminPhoneNumber(user.getUserId());
        Assertions.assertEquals("0", userService.getAdminPhoneNumber(user));
    }

}
