package com.forumsystem.services.contracts;

import com.forumsystem.models.Comment;
import com.forumsystem.models.User;
import com.forumsystem.models.modeldto.CommentResponseDto;

import java.util.List;

public interface CommentService {

    List<CommentResponseDto> getAll(int post_id);

    Comment getById(int id);

    void create(int user_id, Comment comment, int post_id);

    void update(User user, Comment comment);

    void delete(User user, Comment comment);
}
