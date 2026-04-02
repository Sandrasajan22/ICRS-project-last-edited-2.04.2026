package com.main.icrsbackend.model.technicaltest;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "technical_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicalQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stream;
    private String skill;
    private String level;

    @Column(name = "set_number")
    private Integer setNumber;

    @Column(columnDefinition = "TEXT")
    private String question;

    private String option1;
    private String option2;
    private String option3;
    private String option4;

    @Column(name = "correct_answer")
    private String correctAnswer;

    private String difficulty;

    @Column(name = "question_type")
    private String questionType;
}