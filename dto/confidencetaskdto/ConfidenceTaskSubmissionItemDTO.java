package com.main.icrsbackend.dto.confidencetaskdto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfidenceTaskSubmissionItemDTO {

    private String task;
    private String instruction;

    @JsonAlias({"expectedTime", "expected_time"})
    private Integer expectedTime;

    private String difficulty;

    @JsonAlias({"skillFocus", "skill_focus"})
    private String skillFocus;

    private Boolean completed;

    @JsonAlias({"timeTaken", "time_taken"})
    private Integer timeTaken;

    @JsonAlias({"selfRating", "self_rating"})
    private Integer selfRating;
}