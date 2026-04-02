package com.main.icrsbackend.repository.feed;

import com.main.icrsbackend.model.feed.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findByUserId(Long userId);
    long countByUserId(Long userId);
}
