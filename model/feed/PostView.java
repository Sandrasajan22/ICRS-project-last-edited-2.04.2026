package com.main.icrsbackend.model.feed;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_views")
@Getter
@Setter
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @PrePersist
    public void onCreate() {
        if (viewedAt == null) viewedAt = LocalDateTime.now();
    }
}