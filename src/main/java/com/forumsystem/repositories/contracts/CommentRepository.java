package com.forumsystem.repositories.contracts;

import com.forumsystem.models.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> getAll(int postId);

    Comment getById(int id);

    void create(Comment comment);

    void update(Comment comment);

    void delete(Comment comment);
}
