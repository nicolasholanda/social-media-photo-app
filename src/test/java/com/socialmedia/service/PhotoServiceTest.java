package com.socialmedia.service;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.PhotoRepository;
import com.socialmedia.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PhotoService photoService;

    @Test
    void upload_savesPhotoWithAuthorAndTags() throws IOException {
        User author = new User();
        author.setUsername("alice");
        when(userService.findByUsername("alice")).thenReturn(author);
        when(tagRepository.findByName(any())).thenReturn(Optional.empty());
        when(tagRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(photoRepository.save(any(Photo.class))).thenAnswer(inv -> inv.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        Photo photo = photoService.upload(file, "Nice view", "nature,travel", "alice");

        assertThat(photo.getAuthor()).isEqualTo(author);
        assertThat(photo.getCaption()).isEqualTo("Nice view");
        assertThat(photo.getTags()).hasSize(2);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(photoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> photoService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Photo not found");
    }

    @Test
    void delete_throwsWhenNotOwner() {
        User owner = new User();
        owner.setUsername("alice");
        Photo photo = new Photo();
        photo.setAuthor(owner);
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        assertThatThrownBy(() -> photoService.delete(1L, "bob"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not authorized");
    }

    @Test
    void delete_succeedsWhenOwner() {
        User owner = new User();
        owner.setUsername("alice");
        Photo photo = new Photo();
        photo.setAuthor(owner);
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        assertThatNoException().isThrownBy(() -> photoService.delete(1L, "alice"));
        verify(photoRepository).delete(photo);
    }
}
