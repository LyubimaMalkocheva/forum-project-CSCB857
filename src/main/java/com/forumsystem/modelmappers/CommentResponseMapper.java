package com.forumsystem.modelmappers;

import com.forumsystem.models.Comment;
import com.forumsystem.models.modeldto.CommentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CommentResponseMapper {

    public CommentResponseDto convertToDTO(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();

        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedBy(comment.getUser().getUsername());

        return dto;
    }
}
