package com.forumsystem.models.modeldto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.forumsystem.modelhelpers.ModelConstantHelper.*;
import static com.forumsystem.modelhelpers.ModelConstantHelper.INVALID_CONTENT_LENGTH_ERROR_MESSAGE;

public class PostDtoMvc {

    @NotEmpty(message = EMPTY_TITLE_ERROR_MESSAGE)
    @Size(min = 16, max = 64, message = INVALID_TITLE_LENGTH_ERROR_MESSAGE)
    private String title;
    @NotEmpty(message = EMPTY_CONTENT_ERROR_MESSAGE)
    @Size(min = 32, max = 8192, message = INVALID_CONTENT_LENGTH_ERROR_MESSAGE)
    private String content;

    @Pattern(regexp = "^$|^(#[a-z]+)(\\s+#[a-z]+)*$", message = "Each tag must start with a '#' followed by a lowercase letters and be separated by spaces.")
    private String tags;

    public PostDtoMvc() {
    }

    public PostDtoMvc(String title, String content, String tagList) {
        this.title = title;
        this.content = content;
        this.tags = tagList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
