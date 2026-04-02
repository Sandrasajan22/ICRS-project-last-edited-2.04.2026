package com.main.icrsbackend.dto;

import com.main.icrsbackend.dto.feed.PostDTO;
import com.main.icrsbackend.model.feed.MediaType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import java.util.List;

@Data
public class ProfileResponse {

    private Long userId;
    private String fullName;
    private String headline;
    private String location;
    private String email;
    private String profilePhoto;
    private String bio;
    private String description;

    private String category;     // Soft Skills, Leadership...
    private String mode;         // Online / Offline / Hybrid
    private String duration;     // "4 sessions / 2 weeks"
    private String fee;          // keep String because UI sends "₹999"
    private String title;

    private String issuer;
    private String year;

    // store "/uploads/certifications/<file>"
    private String fileUrl;
    private long followers;
    private long following;

    private String role;
    private Long id;


    private String caption;

    private String tags;

    private String mediaUrl;

    private MediaType mediaType = MediaType.NONE;

    private List<PostDTO> posts;
    private String degree;

    private String field;

    private String college;

    private String university;


    private String marks;

    private List<String> skills;   // optional
}