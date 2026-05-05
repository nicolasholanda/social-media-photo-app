package com.socialmedia.controller;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.service.PhotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhotoController.class)
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoService photoService;

    @Test
    @WithMockUser
    void timeline_returnsTimelineView() throws Exception {
        when(photoService.getTimeline(any())).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 12), 0));

        mockMvc.perform(get("/timeline"))
                .andExpect(status().isOk())
                .andExpect(view().name("photo/timeline"));
    }

    @Test
    @WithMockUser
    void photoDetail_returnsDetailView() throws Exception {
        User author = new User();
        author.setUsername("alice");
        Photo photo = new Photo();
        photo.setAuthor(author);
        when(photoService.findById(1L)).thenReturn(photo);

        mockMvc.perform(get("/photos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("photo/detail"))
                .andExpect(model().attributeExists("photo"));
    }

    @Test
    @WithMockUser
    void uploadPage_returnsUploadView() throws Exception {
        mockMvc.perform(get("/upload"))
                .andExpect(status().isOk())
                .andExpect(view().name("photo/upload"));
    }

    @Test
    @WithMockUser
    void deletePhoto_redirectsToTimeline() throws Exception {
        mockMvc.perform(post("/photos/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"));

        verify(photoService).delete(eq(1L), any());
    }
}
