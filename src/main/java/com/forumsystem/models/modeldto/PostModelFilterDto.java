package com.forumsystem.models.modeldto;


public class PostModelFilterDto {
    private String title;
    private Integer likes;
    private Integer dislikes;
    private String tagName;
    private String sortBy;
    private String sortOrder;

    public PostModelFilterDto() {
    }

    public PostModelFilterDto(String title,
                              Integer likes,
                              Integer dislikes,
                              String tagName,
                              String sortBy,
                              String sortOrder) {
        this.title = title;
        this.likes = likes;
        this.dislikes = dislikes;
        this.tagName = tagName;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
