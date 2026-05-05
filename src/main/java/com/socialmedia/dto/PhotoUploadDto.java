package com.socialmedia.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PhotoUploadDto {

    @Size(max = 200)
    private String caption;

    private String tags;

    private MultipartFile file;
}
