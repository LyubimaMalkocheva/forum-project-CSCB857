package com.forumsystem.services;

import com.forumsystem.models.Tag;
import com.forumsystem.models.User;
import com.forumsystem.repositories.contracts.TagRepository;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.еxceptions.EntityNotFoundException;
import com.forumsystem.еxceptions.UnauthorizedOperationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class TagServiceTests {
    @Mock
    TagRepository tagRepository;
    @InjectMocks
    TagServiceImpl tagService;

    @Mock
    UserRepository userRepository;


    @Test
    void getAll_Should_ThrowUnauthorizedOperationException_IfUserNotAdmin() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> tagService.getAll(user));
        Mockito.verify(userRepository, Mockito.times(1)).checkIfAdmin(user.getUserId());
    }

    @Test
    void getAll_Should_ReturnTags_IfUserIsAdmin() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);
        Mockito.when(tagRepository.getAll()).thenReturn(Arrays.asList(new Tag(), new Tag()));

        // Act
        List<Tag> result = tagService.getAll(user);

        // Assert
        Assertions.assertEquals(2, result.size());
        Mockito.verify(userRepository, Mockito.times(1)).checkIfAdmin(user.getUserId());
        Mockito.verify(tagRepository, Mockito.times(1)).getAll();
    }

    @Test
    void getAll_Should_CallRepository() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        Mockito.when(userRepository.checkIfAdmin(user.getUserId())).thenReturn(true);
        Mockito.when(tagRepository.getAll()).thenReturn(Arrays.asList(new Tag(), new Tag()));

        // Act
        tagService.getAll(user);

        // Assert
        Mockito.verify(tagRepository, Mockito.times(1)).getAll();
    }

    @Test
    void getById_Should_ReturnTag_When_TagExists() {
        // Arrange
        int tagId = 1;
        Mockito.when(tagRepository.getById(tagId)).thenReturn(new Tag());

        // Act
        Tag result = tagService.getById(tagId);

        // Assert
        Assertions.assertNotNull(result);
    }

    @Test
    void getById_Should_ThrowEntityNotFoundException_When_TagDoesNotExist() {
        // Arrange
        int tagId = 1;
        Mockito.when(tagRepository.getById(tagId)).thenThrow(EntityNotFoundException.class);

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> tagService.getById(tagId));
    }


    @Test
    void getById_Should_CallRepository() {
        // Arrange
        int tagId = 1;
        Mockito.when(tagRepository.getById(tagId)).thenReturn(new Tag());

        // Act
        tagService.getById(tagId);

        // Assert
        Mockito.verify(tagRepository, Mockito.times(1)).getById(tagId);
    }

    @Test
    void getByName_Should_CallRepository() {
        // Arrange
        String tagName = "tag";
        Mockito.when(tagRepository.getByName(tagName)).thenReturn(new Tag());

        // Act
        tagService.getByName(tagName);

        // Assert
        Mockito.verify(tagRepository, Mockito.times(1)).getByName(tagName);
    }

    @Test
    void getByName_Should_ReturnTag_When_TagExists() {
        // Arrange
        String tagName = "tag";
        Mockito.when(tagRepository.getByName(tagName)).thenReturn(new Tag());

        // Act
        Tag result = tagService.getByName(tagName);

        // Assert
        Assertions.assertNotNull(result);
    }

    @Test
    void getByName_Should_ThrowEntityNotFoundException_When_TagDoesNotExist() {
        // Arrange
        String tagName = "tag";
        Mockito.when(tagRepository.getByName(tagName)).thenThrow(EntityNotFoundException.class);

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> tagService.getByName(tagName));
    }

    @Test
    void create_Should_CallRepository_When_TagSetIsEmpty() {
        // Arrange
        Set<Tag> tagSet = new HashSet<>();

        // Act
        tagService.create(tagSet);

        // Assert
        Mockito.verify(tagRepository, Mockito.never())
                .create(Mockito.any(Tag.class));
    }


    @Test
    void create_Should_CallRepository_When_TagWithSameNameDoesNotExist() {
        // Arrange
        Set<Tag> tagSet = new HashSet<>();
        Tag tag = new Tag();
        tag.setName("tag");
        tagSet.add(tag);

        Mockito.when(tagRepository.getByName(tag.getName()))
                .thenThrow(EntityNotFoundException.class)
                .thenReturn(new Tag());

        // Act
        tagService.create(tagSet);

        // Assert
        Mockito.verify(tagRepository, Mockito.times(1))
                .create(tag);
    }

    @Test
    void create_Should_NotCallRepository_When_TagWithSameNameExists() {
        // Arrange
        Set<Tag> tagSet = new HashSet<>();
        Tag tag = new Tag();
        tag.setName("tag");
        tagSet.add(tag);

        Mockito.when(tagRepository.getByName(tag.getName()))
                .thenReturn(tag);

        // Act
        tagService.create(tagSet);

        // Assert
        Mockito.verify(tagRepository, Mockito.never())
                .create(tag);
    }

    @Test
    void delete_Should_CallRepository() {
        // Arrange
        int tagId = 1;
        Tag tag = new Tag();
        Mockito.when(tagRepository.getById(tagId)).thenReturn(tag);
        Mockito.doNothing().when(tagRepository).delete(tag);

        // Act
        tagService.delete(new User(), tagId);

        // Assert
        Mockito.verify(tagRepository, Mockito.times(1)).delete(tag);
    }

    @Test
    void delete_Should_ThrowEntityNotFoundException_When_TagDoesNotExist() {
        // Arrange
        int tagId = 1;
        Mockito.when(tagRepository.getById(tagId)).thenThrow(EntityNotFoundException.class);

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> tagService.delete(new User(), tagId));
    }

}
