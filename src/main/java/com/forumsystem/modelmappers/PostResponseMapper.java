package com.forumsystem.modelmappers;

import com.forumsystem.models.Post;
import com.forumsystem.models.Tag;
import com.forumsystem.models.modeldto.PostResponseDto;
import com.forumsystem.models.modeldto.PostResponseDtoMvc;
import com.forumsystem.models.modeldto.TagDto;
import com.forumsystem.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PostResponseMapper {

    private final CommentService commentService;

    private final TagMapper tagMapper;
    @Autowired
    public PostResponseMapper(CommentService commentService, TagMapper tagMapper) {
        this.commentService = commentService;
        this.tagMapper = tagMapper;
    }


    public List<PostResponseDto> convertToDTO(List<Post> postList) {
        List<PostResponseDto> result = new ArrayList<>();
        for (Post postLocal : postList) {
            result.add(convertToDTO(postLocal));
        }
        return result;
    }

    public PostResponseDto convertToDTO(Post post) {
        PostResponseDto dto = new PostResponseDto();

        dto.setPostId(post.getPostId());
        dto.setCreatedBy(post.getCreatedBy().getUsername());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikes(post.getLikes());
        dto.setDislikes(post.getDislikes());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setTags(convertTagList(post.getPostTags()));
        dto.setComments(commentService.getAll(post.getPostId()));

        return dto;
    }
    private  List<TagDto> convertTagList(Set<Tag> tags){
        List<TagDto> result = new ArrayList<>();
        if(tags!=null && !tags.isEmpty()) {
            for (Tag tag : tags) {
                TagDto dto = new TagDto();
                dto.setName(tag.getName());
                result.add(dto);
            }
        }
        return result;
    }

    public PostResponseDtoMvc convertToDtoUpdate(Post post) {
        PostResponseDtoMvc dto = new PostResponseDtoMvc();

        dto.setPostId(post.getPostId());
        dto.setCreatedBy(post.getCreatedBy().getUsername());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikes(post.getLikes());
        dto.setDislikes(post.getDislikes());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setTags(convertTagListToString(post.getPostTags()));
        dto.setComments(commentService.getAll(post.getPostId()));

        return dto;
    }
    private  String convertTagListToString(Set<Tag> tags){
        StringBuilder tagString = new StringBuilder();

        if(tags!=null && !tags.isEmpty()) {
            for (Tag tag : tags) {
                tagString.append(tag.getName()).append(" ");
            }
        }
        return tagString.toString().trim();
    }
}
