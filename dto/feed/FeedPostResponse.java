package com.main.icrsbackend.dto.feed;

import com.main.icrsbackend.model.feed.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FeedPostResponse {

    private Long id;

    private Long userId;
    private String firstName;
    private String userName;
    private String profileImage;

    private String caption;
    private String tags;
    private String mediaUrl;
    private MediaType mediaType;

    private String aspectRatio;
    private Double cropX;
    private Double cropY;
    private Double cropZoom;

    private LocalDateTime createdAt;

    private long likeCount;
    private long commentCount;
    private long viewCount;
    private boolean likedByCurrentUser;
}