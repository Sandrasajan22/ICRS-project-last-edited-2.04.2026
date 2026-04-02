package com.main.icrsbackend.dto.feed;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FeedPageResponse {
    private List<FeedPostResponse> posts;
    private boolean hasMore;
}