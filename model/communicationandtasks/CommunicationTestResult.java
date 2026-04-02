package com.main.icrsbackend.model.communicationandtasks;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name = "communication_test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunicationTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String name;

    private String stream;
    private String level;

    private Integer setNumber;

    private Integer score;
    private Integer totalQuestions;

    private String finalEvaluation;

    private LocalDateTime submittedAt;
}


