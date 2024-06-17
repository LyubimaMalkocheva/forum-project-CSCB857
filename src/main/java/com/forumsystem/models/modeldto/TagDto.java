package com.forumsystem.models.modeldto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TagDto {
    @NotEmpty(message = "Tag cannot be empty")
    @Size(min = 2, max = 50,
            message = "Tag cannot be less than 1 or more than 50 letters")
    @Pattern(regexp = "r'#\\S+'", message = "Tag must start with a #")
    @Pattern(regexp = "^[a-z0-9_\\-]+$", message = "Tag must be with lower case letters only")
    String name;

    public TagDto() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
