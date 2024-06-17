package com.forumsystem.modelhelpers;

import java.util.Optional;

public class PostModelFilterOptions {

    private Optional<String> title;
    private Optional<Integer> likes;
    private Optional<Integer> dislikes;
    private Optional<String> tagName;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public PostModelFilterOptions(String title,
                                  Integer likes,
                                  Integer dislikes,
                                  String tagName,
                                  String sortBy,
                                  String sortOrder) {
        this.title = Optional.ofNullable(title);
        this.likes = Optional.ofNullable(likes);
        this.tagName = Optional.ofNullable(tagName);
        this.dislikes = Optional.ofNullable(dislikes);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);

    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(Optional<String> sortBy) {
        this.sortBy = sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Optional<String> sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public void setTitle(Optional<String> title) {
        this.title = title;
    }

    public Optional<Integer> getLikes() {
        return likes;
    }

    public void setLikes(Optional<Integer> likes) {
        this.likes = likes;
    }

    public Optional<Integer> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Optional<Integer> dislikes) {
        this.dislikes = dislikes;
    }
    public Optional<String> getTagName() {
        return tagName;
    }

    public void setTagName(Optional<String> tagName) {
        this.tagName = tagName;
    }
}
