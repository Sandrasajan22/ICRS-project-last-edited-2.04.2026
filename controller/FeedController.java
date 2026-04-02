package com.main.icrsbackend.controller;

import com.main.icrsbackend.dto.feed.CommentRequest;
import com.main.icrsbackend.dto.feed.CommentResponse;
import com.main.icrsbackend.dto.feed.FeedPageResponse;
import com.main.icrsbackend.dto.feed.FeedPostResponse;
import com.main.icrsbackend.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public FeedPageResponse getFeed(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return feedService.getFeed(userId, page, size);
    }

    @PostMapping("/{postId}/like")
    public FeedPostResponse toggleLike(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        return feedService.toggleLike(postId, userId);
    }

    @PostMapping("/{postId}/view")
    public void addView(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        feedService.addView(postId, userId);
    }

    @GetMapping("/{postId}/comments")
    public List<CommentResponse> getComments(
            @PathVariable Long postId,
            @RequestParam(required = false) Long viewerId
    ) {
        return feedService.getComments(postId, viewerId);
    }

    @PostMapping("/{postId}/comments")
    public CommentResponse addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request
    ) {
        return feedService.addComment(postId, request);
    }

    @PutMapping("/comments/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request
    ) {
        return feedService.updateComment(commentId, request);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId
    ) {
        feedService.deleteComment(commentId, userId);
    }
}