package com.forumsystem.services;

import com.forumsystem.models.Tag;
import com.forumsystem.models.User;
import com.forumsystem.repositories.contracts.TagRepository;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.services.contracts.TagService;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.forumsystem.modelhelpers.ModelConstantHelper.INSUFFICIENT_PERMISSIONS_ERROR_MESSAGE;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Tag> getAll(User user) {

        if (!userRepository.checkIfAdmin(user.getUserId())){
            throw new UnauthorizedOperationException(INSUFFICIENT_PERMISSIONS_ERROR_MESSAGE);
        }
        return tagRepository.getAll();
    }

    @Override
    public Tag getById(int tagId) {
        return tagRepository.getById(tagId);
    }

    @Override
    public Tag getByName(String tagName) {
        return tagRepository.getByName(tagName);
    }

    @Override
    public void create(Set<Tag> tagSet) {
        for (Tag postTag : tagSet) {
            try {
            tagRepository.getByName(postTag.getName());
            } catch (EntityNotFoundException e) {
                tagRepository.create(postTag);
            }
            postTag.setId(tagRepository.getByName(postTag.getName()).getId());
        }
    }

    @Override
    public void delete(User user, int tagId) {
        Tag tagToBeDeleted = tagRepository.getById(tagId);
        tagRepository.delete(tagToBeDeleted);
    }
}
