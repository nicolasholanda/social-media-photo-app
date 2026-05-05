package com.socialmedia.service;

import com.socialmedia.entity.Comment;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PhotoService photoService;
    private final UserService userService;

    @Transactional
    public Comment addComment(Long photoId, String content, String username) {
        Photo photo = photoService.findById(photoId);
        User author = userService.findByUsername(username);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPhoto(photo);
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new IllegalStateException("Not authorized to delete this comment");
        }
        commentRepository.delete(comment);
    }
}
