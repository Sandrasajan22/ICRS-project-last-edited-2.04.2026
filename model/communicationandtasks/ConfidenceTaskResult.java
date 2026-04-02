package com.main.icrsbackend.model.communicationandtasks;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "confidence_task_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfidenceTaskResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String name;
    private String level;

    @Column(name = "set_number")
    private Integer setNumber;

    private Integer completionScore;
    private Integer confidenceScore;
    private Integer consistencyScore;
    private String finalEvaluation;

    @Column(columnDefinition = "TEXT")
    private String submissionJson;

    private LocalDateTime submittedAt;
}