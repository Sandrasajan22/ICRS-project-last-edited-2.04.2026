package com.main.icrsbackend.dto.feed;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentResponse {
    private Long id;
    private Long postId;
    private Long userId;
    private String firstName;
    private String userName;
    private String profileImage;
    private String content;
    private LocalDateTime createdAt;
    private boolean owner;
}