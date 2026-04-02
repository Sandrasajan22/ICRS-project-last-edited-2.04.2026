package com.main.icrsbackend.model.technicaltest;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "technical_test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicalTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;

    private String stream;

    private String skill;

    private String level;

    @Column(name = "set_number")
    private Integer setNumber;

    private Integer score;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "final_evaluation")
    private String finalEvaluation;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
}