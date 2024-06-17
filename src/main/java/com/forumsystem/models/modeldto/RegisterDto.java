package com.forumsystem.models.modeldto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.forumsystem.modelhelpers.ModelConstantHelper.*;


public class RegisterDto {
    @NotEmpty(message = "Username can't be empty.")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,16}$",
            message = USERNAME_ERROR_MESSAGE)
    private String username;
    @NotEmpty(message = "Password can't be empty.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{4,20}$",
            message = PASSWORD_ERROR_MESSAGE)
    private String password;


    public void setPassword(String password) {
        this.password = password;
    }

    @NotEmpty(message = "Password confirmation can't be empty.")
    private String passwordConfirm;
    @Size(min = 4, max = 32, message = NAME_ERROR_MESSAGE)
    @NotEmpty(message = "First name can't be empty.")
    private String firstName;
    @Size(min = 4, max = 32, message = NAME_ERROR_MESSAGE)

    @NotEmpty(message = "Last name can't be empty.")
    private String lastName;
    @Email(
            message = INVALID_EMAIL_ERROR_MESSAGE,
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    @NotEmpty(message = "Email can't be empty.")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
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
