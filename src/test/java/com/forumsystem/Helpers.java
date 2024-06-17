package com.forumsystem;

import com.forumsystem.modelhelpers.PostModelFilterOptions;
import com.forumsystem.modelhelpers.UserModelFilterOptions;
import com.forumsystem.models.User;

public class Helpers {

    public static PostModelFilterOptions createMockPostFilterOptions() {
        return new PostModelFilterOptions(
                "title",
                0,
                0,
                "tag",
                "sort",
                "order");
    }

    public static UserModelFilterOptions createMockUserFilterOptions(){
        return new UserModelFilterOptions("username",
                "email",
                "firstName",
                "sort",
                "order");
    }

    public static User createMockAdminUser() {

        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setEmail("mock@user.com");
        mockUser.setUsername("mockUsername");
        mockUser.setLastName("mockLastName");
        mockUser.setPassword("mockPassword");
        mockUser.setFirstName("mockFirstName");
        return mockUser;
    }

    public static User createMockUser() {

        User mockUser = new User();
        mockUser.setUserId(2);
        mockUser.setEmail("mock@user.com");
        mockUser.setUsername("mockUsername");
        mockUser.setLastName("mockLastName");
        mockUser.setPassword("mockPassword");
        mockUser.setFirstName("mockFirstName");
        return mockUser;
    }
}
