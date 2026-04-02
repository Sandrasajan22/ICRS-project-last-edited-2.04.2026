package com.main.icrsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPublicDTO {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String role;
    private String profileImage;
    private boolean isFollowing;
}