package com.main.icrsbackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfileDTO {

    private Long userId;

    private String fname;
    private String lname;
    private String fullName;

    private String email;

    private String phone;
    private String location;
    private String headline;
    private String bio;

    private List<String> skills;

    private String linkedin;
    private String github;
    private String portfolio;

    private String profilePhoto;
    private boolean isInitial;

    private String role;
    private long postCount;
    private long followerCount;
    private long followingCount;
}