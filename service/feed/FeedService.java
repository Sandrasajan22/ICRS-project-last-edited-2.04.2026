package com.main.icrsbackend.service.feed;

import com.main.icrsbackend.dto.feed.CommentRequest;
import com.main.icrsbackend.dto.feed.CommentResponse;
import com.main.icrsbackend.dto.feed.FeedPageResponse;
import com.main.icrsbackend.dto.feed.FeedPostResponse;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.feed.Post;
import com.main.icrsbackend.model.feed.PostComment;
import com.main.icrsbackend.model.feed.PostLike;
import com.main.icrsbackend.model.feed.PostView;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.feed.PostCommentRepository;
import com.main.icrsbackend.repository.feed.PostLikeRepository;
import com.main.icrsbackend.repository.feed.PostRepository;
import com.main.icrsbackend.repository.feed.PostViewRepository;
import com.main.icrsbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostViewRepository postViewRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final FeedUserMapper feedUserMapper;
    private final PostService postService;
    private final NotificationService notificationService;

    public FeedPageResponse getFeed(Long currentUserId, int page, int size) {
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));

        List<FeedPostResponse> posts = postPage.getContent()
                .stream()
                .map(post -> postService.mapPost(post, currentUserId))
                .toList();

        return FeedPageResponse.builder()
                .posts(posts)
                .hasMore(!postPage.isLast())
                .build();
    }

    public FeedPostResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadyLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);

        if (alreadyLiked) {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        } else {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            postLikeRepository.save(like);

            // Notify post owner (not self-likes)
            if (!post.getUserId().equals(userId)) {
                User liker = userRepository.findById(userId).orElse(null);
                String likerName = (liker != null && liker.getFname() != null) ? liker.getFname() : "Someone";
                notificationService.create(
                        post.getUserId(),
                        likerName + " liked your post",
                        "LIKE",
                        "/posts/" + post.getId(),
                        postId
                );
            }
        }

        return postService.mapPost(post, userId);
    }

    public void addView(Long postId, Long userId) {
        boolean alreadyViewed = postViewRepository.existsByPostIdAndUserId(postId, userId);

        if (!alreadyViewed) {
            PostView view = new PostView();
            view.setPostId(postId);
            view.setUserId(userId);
            postViewRepository.save(view);
        }
    }

    public List<CommentResponse> getComments(Long postId, Long viewerId) {
        return postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> mapComment(comment, viewerId))
                .toList();
    }

    public CommentResponse addComment(Long postId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUser(user);
        comment.setContent(request.getContent());

        postCommentRepository.save(comment);

        // Notify post owner (not self-comments)
        if (!post.getUserId().equals(request.getUserId())) {
            notificationService.create(
                    post.getUserId(),
                    user.getFname() + " commented on your post",
                    "COMMENT",
                    "/posts/" + postId,
                    postId
            );
        }

        return mapComment(comment, request.getUserId());
    }

    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        PostComment comment = postCommentRepository.findByIdAndUser_Id(commentId, request.getUserId())
                .orElseThrow(() -> new RuntimeException("Comment not found or not yours"));

        comment.setContent(request.getContent());
        postCommentRepository.save(comment);

        return mapComment(comment, request.getUserId());
    }

    public void deleteComment(Long commentId, Long userId) {
        PostComment comment = postCommentRepository.findByIdAndUser_Id(commentId, userId)
                .orElseThrow(() -> new RuntimeException("Comment not found or not yours"));

        postCommentRepository.delete(comment);
    }

    private CommentResponse mapComment(PostComment comment, Long viewerId) {
        User user = comment.getUser();

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(user.getId())
                .firstName(feedUserMapper.getFirstName(user))
                .userName(feedUserMapper.getDisplayName(user))
                .profileImage(feedUserMapper.getProfileImage(user))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .owner(viewerId != null && viewerId.equals(user.getId()))
                .build();
    }
}