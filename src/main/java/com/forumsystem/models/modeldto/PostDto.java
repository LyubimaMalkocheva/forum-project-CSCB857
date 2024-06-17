package com.forumsystem.models.modeldto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import static com.forumsystem.modelhelpers.ModelConstantHelper.*;

public class PostDto {

    @NotEmpty(message = EMPTY_TITLE_ERROR_MESSAGE)
    @Size(min = 16, max = 64, message = INVALID_TITLE_LENGTH_ERROR_MESSAGE)
    private String title;
    @NotEmpty(message = EMPTY_CONTENT_ERROR_MESSAGE)
    @Size(min = 32, max = 8192, message = INVALID_CONTENT_LENGTH_ERROR_MESSAGE)
    private String content;

    private List<TagDto> tagList;

    public PostDto() {
         this.tagList = new ArrayList<>();
    }

    public PostDto(String title, String content) {
        this.title = title;
        this.content = content;
        this.tagList = new ArrayList<>();
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

    public List<TagDto> getTagList() {
        return new ArrayList<>(tagList);
    }

    public void setTagList(List<TagDto> tagList) {
        if(tagList != null && tagList.isEmpty()){
            this.tagList = tagList;
        }
    }


}
