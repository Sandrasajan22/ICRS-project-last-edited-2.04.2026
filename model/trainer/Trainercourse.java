package com.main.icrsbackend.model.trainer;

import com.main.icrsbackend.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trainer_courses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Trainercourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // many skill trainings per trainer
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // UI fields
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;     // Soft Skills, Leadership...
    private String mode;         // Online / Offline / Hybrid
    private String duration;     // "4 sessions / 2 weeks"
    private String fee;          // keep String because UI sends "₹999"
    private String location;     // optional

    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (mode == null || mode.isBlank()) mode = "Online";
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}