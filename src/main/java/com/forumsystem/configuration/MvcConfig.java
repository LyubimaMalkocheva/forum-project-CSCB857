package com.forumsystem.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path userUploadedProfilePictureDir = Paths.get("./user-profilePictures");
        String userProfilePicturePath = userUploadedProfilePictureDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/user-profilePictures/**")
                .addResourceLocations("file:/" + userProfilePicturePath + "/");
    }
}
