package com.socialmedia.controller;

import com.socialmedia.dto.CommentDto;
import com.socialmedia.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/photos/{photoId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public String addComment(@PathVariable Long photoId,
                             @Valid @ModelAttribute("commentForm") CommentDto form,
                             BindingResult result,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentError", "Comment cannot be empty or too long");
            return "redirect:/photos/" + photoId;
        }
        commentService.addComment(photoId, form.getContent(), principal.getName());
        return "redirect:/photos/" + photoId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long photoId,
                                @PathVariable Long commentId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            commentService.deleteComment(commentId, principal.getName());
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/photos/" + photoId;
    }
}
