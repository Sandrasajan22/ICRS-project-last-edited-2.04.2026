package com.main.icrsbackend.dto.feed;

import com.main.icrsbackend.model.feed.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Builder
@Data
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String caption;
    private String tags;
    private String mediaUrl;
    private MediaType mediaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}