package com.forumsystem.modelmappers;

import com.forumsystem.models.Tag;
import com.forumsystem.models.modeldto.TagDto;
import com.forumsystem.repositories.contracts.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TagMapper {
    private final TagRepository tagRepository;

    @Autowired
    public TagMapper(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public Tag fromDto(TagDto tagDto, int id){
        Tag tag = tagRepository.getById(id);
        dtoToObj(tag, tagDto);
        return tag;
    }

    public Tag fromDto (TagDto tagDto){
        Tag tag = new Tag();
        dtoToObj(tag, tagDto);
        return tag;
    }

    public Set<Tag> fromDto(List<TagDto> tagList) {
        Set<Tag> updatedTagSet = new HashSet<>();
        for (TagDto tagDto : tagList) {
            Tag tag = new Tag();
            tag.setName(tagDto.getName());
            tag.setArchived(false);
            updatedTagSet.add(tag);
        }
        return updatedTagSet;
    }

    public Set<Tag> fromDto(String tagList) {
        Set<Tag> updatedTagSet = new HashSet<>();
        String[] tags = tagList.split(" ");
        for (String tagName : tags) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tag.setArchived(false);
            updatedTagSet.add(tag);
        }
        return updatedTagSet;
    }


    private void dtoToObj(Tag tag, TagDto tagDto){
        tag.setName(tagDto.getName());
    }
}
