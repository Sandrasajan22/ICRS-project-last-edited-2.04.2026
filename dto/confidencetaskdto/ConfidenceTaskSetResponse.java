package com.main.icrsbackend.dto.confidencetaskdto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfidenceTaskSetResponse {
    private String type;
    private String level;
    private Integer set_number;
    private List<ConfidenceTaskDTO> tasks;
}