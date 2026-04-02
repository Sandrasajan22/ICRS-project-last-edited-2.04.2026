package com.main.icrsbackend.controller;

import com.main.icrsbackend.dto.feed.FeedPostResponse;
import com.main.icrsbackend.service.feed.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FeedPostResponse createPost(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "") String caption,
            @RequestParam(required = false, defaultValue = "") String tags,
            @RequestParam(required = false, defaultValue = "4:5") String aspectRatio,
            @RequestParam(required = false, defaultValue = "50") Double cropX,
            @RequestParam(required = false, defaultValue = "50") Double cropY,
            @RequestParam(required = false, defaultValue = "1") Double cropZoom,
            @RequestPart(required = false) MultipartFile file
    ) {
        return postService.createPost(userId, caption, tags, aspectRatio, cropX, cropY, cropZoom, file);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FeedPostResponse updatePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "") String caption,
            @RequestParam(required = false, defaultValue = "") String tags,
            @RequestParam(required = false, defaultValue = "4:5") String aspectRatio,
            @RequestParam(required = false, defaultValue = "50") Double cropX,
            @RequestParam(required = false, defaultValue = "50") Double cropY,
            @RequestParam(required = false, defaultValue = "1") Double cropZoom,
            @RequestPart(required = false) MultipartFile file
    ) {
        return postService.updatePost(postId, userId, caption, tags, aspectRatio, cropX, cropY, cropZoom, file);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        postService.deletePost(postId, userId);
    }

    @GetMapping("/mine")
    public java.util.List<FeedPostResponse> myPosts(@RequestParam Long userId) {
        return postService.getMyPosts(userId);
    }
}