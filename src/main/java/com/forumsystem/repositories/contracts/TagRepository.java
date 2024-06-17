package com.forumsystem.repositories.contracts;

import com.forumsystem.models.Tag;

import java.util.List;

public interface TagRepository {

    List<Tag> getAll();

    Tag getById(int tagId);
    Tag getByName(String tagName);

    void create(Tag tag);

    // void update();

    void delete(Tag tag);
}
