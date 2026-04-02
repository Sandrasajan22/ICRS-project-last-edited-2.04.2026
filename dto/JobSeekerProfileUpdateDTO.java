package com.main.icrsbackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfileUpdateDTO {
    private String fullName;
    private String phone;
    private String location;
    private String headline;
    private String bio;

    private List<String> skills;

    private String linkedin;
    private String github;
    private String portfolio;
}