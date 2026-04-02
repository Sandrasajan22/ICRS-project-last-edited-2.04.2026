package com.main.icrsbackend.repository.feed;

import com.main.icrsbackend.model.feed.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);
    long countByPostId(Long postId);
    Optional<PostComment> findByIdAndUser_Id(Long id, Long userId);
}