package com.main.icrsbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerProfileDTO {

    private Long userId;

    private String fname;
    private String lname;

    private String phone;
    private String agency;
    private String location;
    private String state;
    private String about;

    private String profilePhotoUrl;

    private String role;
    private boolean isInitial;
    private long postCount;
    private long followerCount;
    private long followingCount;
}