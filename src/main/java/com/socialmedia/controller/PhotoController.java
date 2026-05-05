package com.socialmedia.controller;

import com.socialmedia.dto.CommentDto;
import com.socialmedia.dto.PhotoUploadDto;
import com.socialmedia.entity.Photo;
import com.socialmedia.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class PhotoController {

    private static final int PAGE_SIZE = 12;

    private final PhotoService photoService;

    @GetMapping("/timeline")
    public String timeline(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String tag,
                           Model model) {
        Page<Photo> photos;
        if (tag != null && !tag.isBlank()) {
            photos = photoService.findByTag(tag, PageRequest.of(page, PAGE_SIZE));
            model.addAttribute("activeTag", tag);
        } else {
            photos = photoService.getTimeline(PageRequest.of(page, PAGE_SIZE));
        }
        model.addAttribute("photos", photos);
        model.addAttribute("currentPage", page);
        return "photo/timeline";
    }

    @GetMapping("/photos/{id}")
    public String photoDetail(@PathVariable Long id, Model model) {
        Photo photo = photoService.findById(id);
        model.addAttribute("photo", photo);
        model.addAttribute("commentForm", new CommentDto());
        return "photo/detail";
    }

    @GetMapping("/photos/{id}/image")
    public ResponseEntity<byte[]> serveImage(@PathVariable Long id) {
        Photo photo = photoService.findById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(photo.getImageData());
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("form", new PhotoUploadDto());
        return "photo/upload";
    }

    @PostMapping("/upload")
    public String upload(@Valid @ModelAttribute("form") PhotoUploadDto form,
                         BindingResult result,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "photo/upload";
        }
        if (form.getFile() == null || form.getFile().isEmpty()) {
            result.rejectValue("file", "file.required", "Please select an image file");
            return "photo/upload";
        }
        try {
            Photo photo = photoService.upload(form.getFile(), form.getCaption(), form.getTags(), principal.getName());
            redirectAttributes.addFlashAttribute("success", "Photo published!");
            return "redirect:/photos/" + photo.getId();
        } catch (Exception ex) {
            result.reject("upload.error", "Failed to upload photo: " + ex.getMessage());
            return "photo/upload";
        }
    }

    @PostMapping("/photos/{id}/delete")
    public String deletePhoto(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            photoService.delete(id, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Photo deleted.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/timeline";
    }

    @GetMapping("/profile/{username}")
    public String userProfile(@PathVariable String username,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Page<Photo> photos = photoService.findByUser(username, PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("photos", photos);
        model.addAttribute("profileUsername", username);
        model.addAttribute("currentPage", page);
        return "photo/profile";
    }
}
