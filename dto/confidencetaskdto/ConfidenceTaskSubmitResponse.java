package com.main.icrsbackend.dto.confidencetaskdto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfidenceTaskSubmitResponse {
    private Integer completionScore;
    private Integer confidenceScore;
    private Integer consistencyScore;
    private String finalEvaluation;
}