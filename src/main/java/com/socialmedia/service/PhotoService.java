package com.socialmedia.service;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.Tag;
import com.socialmedia.entity.User;
import com.socialmedia.repository.PhotoRepository;
import com.socialmedia.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    @Transactional
    public Photo upload(MultipartFile file, String caption, String tagsRaw, String username) throws IOException {
        User author = userService.findByUsername(username);

        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setImageData(file.getBytes());
        photo.setContentType(file.getContentType());
        photo.setAuthor(author);

        if (tagsRaw != null && !tagsRaw.isBlank()) {
            List<Tag> tags = Arrays.stream(tagsRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(name -> tagRepository.findByName(name).orElseGet(() -> tagRepository.save(new Tag(name))))
                    .toList();
            photo.setTags(tags);
        }

        return photoRepository.save(photo);
    }

    public Photo findById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found: " + id));
    }

    public Page<Photo> getTimeline(Pageable pageable) {
        return photoRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Photo> findByTag(String tagName, Pageable pageable) {
        return photoRepository.findByTagName(tagName, pageable);
    }

    public Page<Photo> findByUser(String username, Pageable pageable) {
        return photoRepository.findByAuthorUsernameOrderByCreatedAtDesc(username, pageable);
    }

    @Transactional
    public void delete(Long id, String username) {
        Photo photo = findById(id);
        if (!photo.getAuthor().getUsername().equals(username)) {
            throw new IllegalStateException("Not authorized to delete this photo");
        }
        photoRepository.delete(photo);
    }
}
