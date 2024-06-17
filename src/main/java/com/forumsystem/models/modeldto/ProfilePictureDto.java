package com.forumsystem.models.modeldto;

import jakarta.persistence.Transient;

public class ProfilePictureDto {
    String pictureName;

    public ProfilePictureDto() {
    }

    public ProfilePictureDto(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    @Transient
    public String getProfilePicturePath(int userId) {
        if (pictureName == null || userId == 0) {
            return null;
        }
        return "/user-profilePictures/" + userId + "/" + pictureName;
    }
}
