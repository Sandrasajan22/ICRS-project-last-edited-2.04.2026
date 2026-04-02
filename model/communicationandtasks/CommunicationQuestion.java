package com.main.icrsbackend.model.communicationandtasks;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "communication_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunicationQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stream;   // Grammar / Vocabulary / etc
    private String level;    // Beginner / Intermediate / Difficult

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