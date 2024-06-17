package com.forumsystem.models.modeldto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CommentDto {
    @NotEmpty
    @Size(min = 2, max = 1000, message =
            "Comment length needs to be no less than 2 symbols and no more than 1000 symbols")
    private String content;

    public CommentDto() {

    }

    public CommentDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
