package com.main.icrsbackend.model.feed;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Column(length = 500)
    private String tags;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", length = 20)
    private MediaType mediaType = MediaType.NONE;

    @Column(name = "aspect_ratio", length = 20)
    private String aspectRatio; // "1:1", "4:5", "16:9", "9:16", "FREE"

    @Column(name = "crop_x")
    private Double cropX = 50.0;

    @Column(name = "crop_y")
    private Double cropY = 50.0;

    @Column(name = "crop_zoom")
    private Double cropZoom = 1.0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (mediaType == null) mediaType = MediaType.NONE;
        if (aspectRatio == null) aspectRatio = "4:5";
        if (cropX == null) cropX = 50.0;
        if (cropY == null) cropY = 50.0;
        if (cropZoom == null) cropZoom = 1.0;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}