package com.main.icrsbackend.dto.profiledto;

import lombok.Data;

@Data
public class EmployerProfileDTO {
    private Long userId;
    private String fname;
    private String lname;
    private String email;
    private String role;
    private boolean verified;
    private String verificationStatus;

    private String companyName;
    private String companyWebsite;
    private String industry;
    private String companySize;
    private String headquarters;
    private String hrName;
    private String designation;
    private String companyDescription;
    private String hiringRoles;
    private String linkedinUrl;
    private String logo;
    private String additionalSkills;

    private boolean isInitial;
    private long postCount;
    private long followerCount;
    private long followingCount;
}