package com.main.icrsbackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileUpdateDTO {

    private String fullName;
    private String phone;
    private String location;
    private String headline;
    private String bio;

    private List<String> skills; // no limit

    private String institution;
    private String department;
    private String semester;

    private String linkedin;
    private String github;
    private String portfolio;
}