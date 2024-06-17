package com.forumsystem.modelmappers;

import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.*;
import com.forumsystem.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final UserService userService;

    @Autowired
    public UserMapper(UserService userService) {
        this.userService = userService;
    }

    public User fromDto(UserDto userDto){
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        return user;
    }

    public User fromDto(UserProfileUpdateDto updatedUser){
        User user = userService.getUserByUsername(updatedUser.getUsername());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        return user;
    }

    public User fromDto(int id, User loggedUser, UpdateUserPasswordDto passwordDto){
        User userWhosePasswordWillBeUpdated = userService.get(id, loggedUser);
        userWhosePasswordWillBeUpdated.setPassword(passwordDto.getNewPassword());
        return userWhosePasswordWillBeUpdated;
    }

    public User fromDto(RegisterDto dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        return user;

    }

    public User fromDto (ProfilePictureDto pictureDto, int id, User loggedUser){
        User user = userService.get(id, loggedUser);
        user.setProfilePicture(pictureDto.getPictureName());
        return user;
    }

    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        user.setPassword(user.getPassword());

        return userDto;
    }

    public AdminDto toAdminDto(User user){
        AdminDto adminDto = new AdminDto();
        adminDto.setPhoneNumber(userService.getAdminPhoneNumber(user));
        return adminDto;
    }

    public UserProfileUpdateDto toUpdateProfileDto(User user){
        UserProfileUpdateDto userDto = new UserProfileUpdateDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public ProfilePictureDto toProfilePictureDto(User currentUser) {
      ProfilePictureDto pictureDto =  new ProfilePictureDto();
      pictureDto.setPictureName(currentUser.getProfilePicture());
      return  pictureDto;
    }

}
