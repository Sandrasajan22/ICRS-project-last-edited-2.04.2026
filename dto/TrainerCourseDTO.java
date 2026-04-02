package com.main.icrsbackend.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TrainerCourseDTO {
    private Long id;
    private Long trainerId;

    private String title;
    private String description;
    private String category;
    private String mode;
    private String duration;
    private String fee;
    private String location;

    private Boolean active;
}