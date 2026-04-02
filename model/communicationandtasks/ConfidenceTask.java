package com.main.icrsbackend.model.communicationandtasks;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "confidence_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfidenceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level;

    @Column(name = "set_number")
    private Integer setNumber;

    @Column(columnDefinition = "TEXT")
    private String task;

    @Column(columnDefinition = "TEXT")
    private String instruction;

    @Column(name = "expected_time")
    private Integer expectedTime;

    private String difficulty;

    @Column(name = "skill_focus")
    private String skillFocus;
}