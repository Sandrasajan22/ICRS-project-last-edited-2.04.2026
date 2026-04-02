package com.main.icrsbackend.dto.profiledto;


import lombok.Data;

@Data
public class MentorProfileDTO {
    private Long userId;
    private String fname;
    private String lname;
    private String email;
    private String role;
    private boolean verified;
    private String verificationStatus;

    private String headline;
    private String specialization;
    private String bio;
    private String skills;
    private String experience;
    private String currentOrganization;
    private String designation;
    private String linkedinUrl;
    private String profileImage;

    private boolean isInitial;
    private long postCount;
    private long followerCount;
    private long followingCount;
}