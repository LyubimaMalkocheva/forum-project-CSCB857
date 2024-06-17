package com.forumsystem.modelmappers;

import com.forumsystem.models.Post;
import com.forumsystem.models.modeldto.PostDto;
import com.forumsystem.models.modeldto.PostDtoMvc;
import com.forumsystem.repositories.contracts.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostMapper {

    private final PostRepository postRepository;
    private final TagMapper tagMapper;

    @Autowired
    public PostMapper(PostRepository postRepository,
                      TagMapper tagMapper) {
        this.postRepository = postRepository;
        this.tagMapper = tagMapper;
    }

    public Post fromDto(PostDto postDto, int id){
        Post post = postRepository.getById(id);
        dtoToObj(post, postDto);
        return post;
    }
    public Post fromDto(PostDto postDto) {
        Post post = new Post();
        dtoToObj(post, postDto);
        return post;
    }

    /**
     * Maps a PostDto to a Post object
     */
    private void dtoToObj(Post post, PostDto postDto) {
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPostTags(tagMapper.fromDto(postDto.getTagList()));
        post.setCreatedAt(LocalDateTime.now());
    }

    /**
     * Maps a PostDtoMvc to a Post object
     * postDtoMvc's tagList are strings separated by spaces
     */
    public Post fromDto(PostDtoMvc postDtoMvc, int id){
        Post post = postRepository.getById(id);
        dtoToObj(post, postDtoMvc);
        return post;
    }


    private void dtoToObj(Post post, PostDtoMvc postDtoMvc) {
        post.setTitle(postDtoMvc.getTitle());
        post.setContent(postDtoMvc.getContent());
        post.setPostTags(tagMapper.fromDto(postDtoMvc.getTags()));
        post.setCreatedAt(LocalDateTime.now());
    }

}
