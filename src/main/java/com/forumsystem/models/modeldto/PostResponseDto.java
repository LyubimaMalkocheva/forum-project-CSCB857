package com.forumsystem.models.modeldto;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PostResponseDto {
    private int postId;
    private String createdBy;
    private String title;

    private String content;

    private int likes;

    private int dislikes;

    private String createdAt;

    private List<CommentResponseDto> comments;

    private List<TagDto> tags;

    public PostResponseDto() {
        this.comments = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public PostResponseDto(String createdBy, String title, String content, int likes, int dislikes, String createdAt, List<CommentResponseDto> comments, List<TagDto> tags) {
        this.createdBy = createdBy;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.dislikes = dislikes;
        this.createdAt = createdAt;
        this.comments = comments;
        this.tags = tags;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime dateTime) {
        this.createdAt = formatDateAndTime(dateTime);
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    private String formatDateAndTime(LocalDateTime dateTime) {
        ZoneId bulgariaZoneId = ZoneId.of("Europe/Sofia");
        ZonedDateTime postDateTime = dateTime.atZone(bulgariaZoneId);
        ZonedDateTime now = ZonedDateTime.now(bulgariaZoneId);

        long minutesDiff = ChronoUnit.MINUTES.between(postDateTime, now);
        long hoursDiff = ChronoUnit.HOURS.between(postDateTime, now);
        long daysDiff = ChronoUnit.DAYS.between(postDateTime, now);


        if (minutesDiff <= 1) {
            return "Just now";
        } else if (minutesDiff < 60) {
            return minutesDiff + " minutes ago";
        } else if (hoursDiff == 1) {
            return hoursDiff + " hour ago";
        } else if (hoursDiff < 24) {
            return hoursDiff + " hours ago";
        } else if (daysDiff == 1) {
            return "Yesterday";
        } else if (daysDiff < 7) {
            return daysDiff + " days ago";
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));
        }
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<CommentResponseDto> getComments() {
        return new ArrayList<>(comments);
    }

    public void setComments(List<CommentResponseDto> comments) {
        if (comments != null) {
            this.comments = comments;
        }
    }

    public List<TagDto> getTags() {
        return new ArrayList<>(tags);
    }

    public void setTags(List<TagDto> tags) {
        if (tags != null) {
            this.tags = tags;
        }
    }
}
