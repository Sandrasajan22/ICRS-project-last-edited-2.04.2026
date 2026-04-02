package com.main.icrsbackend.service.feed;

import com.main.icrsbackend.dto.feed.FeedPostResponse;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.feed.MediaType;
import com.main.icrsbackend.model.feed.Post;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.feed.PostCommentRepository;
import com.main.icrsbackend.repository.feed.PostLikeRepository;
import com.main.icrsbackend.repository.feed.PostRepository;
import com.main.icrsbackend.repository.feed.PostViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostViewRepository postViewRepository;
    private final FeedUserMapper feedUserMapper;
    private final UploadService uploadService;

    public FeedPostResponse createPost(
            Long userId,
            String caption,
            String tags,
            String aspectRatio,
            Double cropX,
            Double cropY,
            Double cropZoom,
            MultipartFile file
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUserId(user.getId());
        post.setCaption(caption == null ? "" : caption.trim());
        post.setTags(tags == null ? "" : tags.trim());
        post.setAspectRatio(aspectRatio);
        post.setCropX(cropX);
        post.setCropY(cropY);
        post.setCropZoom(cropZoom);

        if (file != null && !file.isEmpty()) {
            String mediaUrl = uploadService.save(file);
            post.setMediaUrl(mediaUrl);

            String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
            if (contentType.startsWith("video/")) {
                post.setMediaType(MediaType.VIDEO);
            } else if (contentType.startsWith("image/")) {
                post.setMediaType(MediaType.IMAGE);
            } else {
                post.setMediaType(MediaType.NONE);
            }
        } else {
            post.setMediaType(MediaType.NONE);
            post.setMediaUrl("");
        }

        Post saved = postRepository.save(post);
        return mapPost(saved, userId);
    }

    public FeedPostResponse updatePost(
            Long postId,
            Long userId,
            String caption,
            String tags,
            String aspectRatio,
            Double cropX,
            Double cropY,
            Double cropZoom,
            MultipartFile file
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You can edit only your own post");
        }

        post.setCaption(caption == null ? "" : caption.trim());
        post.setTags(tags == null ? "" : tags.trim());
        post.setAspectRatio(aspectRatio);
        post.setCropX(cropX);
        post.setCropY(cropY);
        post.setCropZoom(cropZoom);

        if (file != null && !file.isEmpty()) {
            String mediaUrl = uploadService.save(file);
            post.setMediaUrl(mediaUrl);

            String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
            if (contentType.startsWith("video/")) {
                post.setMediaType(MediaType.VIDEO);
            } else if (contentType.startsWith("image/")) {
                post.setMediaType(MediaType.IMAGE);
            } else {
                post.setMediaType(MediaType.NONE);
            }
        }

        Post saved = postRepository.save(post);
        return mapPost(saved, userId);
    }

    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You can delete only your own post");
        }

        postRepository.delete(post);
    }

    public List<FeedPostResponse> getMyPosts(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(post -> mapPost(post, userId))
                .toList();
    }

    public FeedPostResponse mapPost(Post post, Long currentUserId) {
        User user = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return FeedPostResponse.builder()
                .id(post.getId())
                .userId(user.getId())
                .firstName(feedUserMapper.getFirstName(user))
                .userName(feedUserMapper.getDisplayName(user))
                .profileImage(feedUserMapper.getProfileImage(user))
                .caption(post.getCaption())
                .tags(post.getTags())
                .mediaUrl(post.getMediaUrl())
                .mediaType(post.getMediaType())
                .aspectRatio(post.getAspectRatio())
                .cropX(post.getCropX())
                .cropY(post.getCropY())
                .cropZoom(post.getCropZoom())
                .createdAt(post.getCreatedAt())
                .likeCount(postLikeRepository.countByPostId(post.getId()))
                .commentCount(postCommentRepository.countByPostId(post.getId()))
                .viewCount(postViewRepository.countByPostId(post.getId()))
                .likedByCurrentUser(
                        currentUserId != null &&
                                postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId)
                )
                .build();
    }
}