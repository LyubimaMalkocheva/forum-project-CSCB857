package com.forumsystem.models.modeldto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import static com.forumsystem.modelhelpers.ModelConstantHelper.*;

public class UserDto {

    @Schema(name = "firstName", example = "Ivan", required = true)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 4, max = 32, message = NAME_ERROR_MESSAGE)
    String firstName;

    @Schema(name = "lastName", example = "Ivanov", required = true)
    @NotNull(message = EMPTY_ERROR_MESSAGE)
    @Size(min = 4, max = 32, message = NAME_ERROR_MESSAGE)
    String lastName;
    @Schema(name = "email", example = "email@email.com", required = true)
    @Email(
            message = EMPTY_ERROR_MESSAGE,
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    @NotEmpty(message = "Email cannot be empty")
    String email;
    @Schema(name = "username", example = "testUsername", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,16}$",
            message = USERNAME_ERROR_MESSAGE)
    String username;
    @Schema(name = "password", example = "Pass1234!", required = true)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{4,20}$",
            message = PASSWORD_ERROR_MESSAGE)
    String password;

    public UserDto() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
