package com.main.icrsbackend.dto.profiledto;
import lombok.Data;

@Data
public class InstitutionProfileDTO {
    private Long userId;
    private String fname;
    private String lname;
    private String email;
    private String role;
    private boolean verified;
    private String verificationStatus;

    private String institutionName;
    private String institutionType;
    private String website;
    private String location;
    private String contactPerson;
    private String designation;
    private String about;
    private String offeredPrograms;
    private String certifications;
    private String logo;

    private boolean isInitial;
    private long postCount;
    private long followerCount;
    private long followingCount;
}