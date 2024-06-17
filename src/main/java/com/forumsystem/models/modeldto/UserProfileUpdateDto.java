package com.forumsystem.models.modeldto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import static com.forumsystem.modelhelpers.ModelConstantHelper.EMPTY_ERROR_MESSAGE;
import static com.forumsystem.modelhelpers.ModelConstantHelper.NAME_ERROR_MESSAGE;

public class UserProfileUpdateDto {
    String username;
    @Schema(name = "firstName", example = "Ivan", required = true)
    @NotEmpty(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 4, max = 32, message = NAME_ERROR_MESSAGE)
    private String firstName;
    @Schema(name = "lastName", example = "Ivanov", required = true)
    @NotEmpty(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 4, max = 32, message = NAME_ERROR_MESSAGE)
    private String lastName;
    @Schema(name = "email", example = "email@email.com", required = true)
//    @Email(
//            message = EMPTY_ERROR_MESSAGE,
//            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
//    )
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    public UserProfileUpdateDto() {
    }

    public UserProfileUpdateDto(String username,
                                String firstName,
                                String lastName,
                                String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
