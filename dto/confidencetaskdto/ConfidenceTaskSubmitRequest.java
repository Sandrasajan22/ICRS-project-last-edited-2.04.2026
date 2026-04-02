package com.main.icrsbackend.dto.confidencetaskdto;



import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfidenceTaskSubmitRequest {

    private Long userId;
    private String name;
    private String level;

    @JsonAlias({"setNumber", "set_number"})
    private Integer setNumber;

    private List<ConfidenceTaskSubmissionItemDTO> tasks;
}