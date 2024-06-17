package com.forumsystem.models.modeldto;

public class CommentResponseDto {

    private int id;
    private String content;
    private String createdBy;

    public CommentResponseDto() {
    }

    public CommentResponseDto(int id, String content, String createdBy) {
        this.id = id;
        this.content = content;
        this.createdBy = createdBy;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
