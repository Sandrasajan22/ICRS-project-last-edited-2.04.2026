package com.main.icrsbackend.dto.confidencetaskdto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfidenceTaskDTO {
    private Long id;
    private String task;
    private String instruction;
    private Integer expected_time;
    private String difficulty;
    private String skill_focus;
}