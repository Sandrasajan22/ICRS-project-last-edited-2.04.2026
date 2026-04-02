package com.main.icrsbackend.dto.feed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Long userId;
    private String content;
}