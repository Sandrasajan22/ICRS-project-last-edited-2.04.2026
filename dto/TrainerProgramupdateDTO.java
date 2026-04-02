package com.main.icrsbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerProgramupdateDTO {

    private Long id;
    private Long trainerId;

    private String title;
    private String institution;
    private String audience;
    private String mode;

    private String date;
    private String time;
    private String duration;
    private String location;
    private String participants;

    private String status;
    private String summary;

    private Boolean active;
}