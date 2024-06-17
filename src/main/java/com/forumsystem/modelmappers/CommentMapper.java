package com.forumsystem.modelmappers;

import com.forumsystem.models.Comment;
import com.forumsystem.models.modeldto.CommentDto;
import com.forumsystem.repositories.contracts.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentMapper(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment fromDto(CommentDto commentDto, int id) {
        Comment comment = commentRepository.getById(id);
        dtoToObj(comment, commentDto);
        return comment;
    }

    public Comment fromDto(CommentDto commentDto) {
        Comment comment = new Comment();
        dtoToObj(comment, commentDto);
        return comment;
    }

    public CommentDto toDto(Comment comment){
        CommentDto commentDto = new CommentDto();
        commentDto.setContent(comment.getContent());
        return commentDto;
    }


    private void dtoToObj(Comment comment, CommentDto commentDto) {
        comment.setContent(commentDto.getContent());
    }
}
