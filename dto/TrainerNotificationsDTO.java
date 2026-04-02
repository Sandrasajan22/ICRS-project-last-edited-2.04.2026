package com.main.icrsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TrainerNotificationsDTO {
    private Long id;
    private String title;
    private String message;
    private String link;
    private boolean read;
    private LocalDateTime createdAt;
}