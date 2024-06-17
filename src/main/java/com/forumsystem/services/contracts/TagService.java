package com.forumsystem.services.contracts;

import com.forumsystem.models.Tag;
import com.forumsystem.models.User;

import java.util.List;
import java.util.Set;

public interface TagService {
    List<Tag> getAll(User user);

    Tag getById(int tagId);
    Tag getByName(String tagName);

    void create(Set<Tag> tagSet);

   // void update();

    void delete(User user, int tagId);
}
