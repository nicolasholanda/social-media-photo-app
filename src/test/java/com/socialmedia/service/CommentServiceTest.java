package com.socialmedia.service;

import com.socialmedia.entity.Comment;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PhotoService photoService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_savesCommentWithAuthorAndPhoto() {
        User author = new User();
        author.setUsername("alice");
        Photo photo = new Photo();
        when(userService.findByUsername("alice")).thenReturn(author);
        when(photoService.findById(1L)).thenReturn(photo);
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        Comment comment = commentService.addComment(1L, "Great shot!", "alice");

        assertThat(comment.getContent()).isEqualTo("Great shot!");
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getPhoto()).isEqualTo(photo);
    }

    @Test
    void deleteComment_throwsWhenNotOwner() {
        User owner = new User();
        owner.setUsername("alice");
        Comment comment = new Comment();
        comment.setAuthor(owner);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(1L, "bob"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not authorized");
    }

    @Test
    void deleteComment_succeedsWhenOwner() {
        User owner = new User();
        owner.setUsername("alice");
        Comment comment = new Comment();
        comment.setAuthor(owner);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThatNoException().isThrownBy(() -> commentService.deleteComment(1L, "alice"));
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_throwsWhenCommentNotFound() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(99L, "alice"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");
    }
}
