package com.main.icrsbackend.model.trainer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trainer_certifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Trainercertifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long trainerId;

    @Column(nullable = false)
    private String title;

    private String issuer;
    private String year;

    // store "/uploads/certifications/<file>"
    private String fileUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}