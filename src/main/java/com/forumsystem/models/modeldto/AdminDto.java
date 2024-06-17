package com.forumsystem.models.modeldto;

import jakarta.validation.constraints.Pattern;

public class AdminDto {
    @Pattern(regexp = "^[0-9]+$", message = "Phone Number must include only digits")
    private String phoneNumber;

    public AdminDto() {
    }

    public AdminDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
