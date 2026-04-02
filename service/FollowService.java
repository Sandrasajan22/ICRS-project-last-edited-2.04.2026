package com.main.icrsbackend.service;

import com.main.icrsbackend.model.Follow;
import com.main.icrsbackend.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository repo;

    public void follow(Long userId, Long targetId) {
        if (!repo.existsByFollowerIdAndFollowingId(userId, targetId)) {
            Follow f = new Follow();
            f.setFollowerId(userId);
            f.setFollowingId(targetId);
            repo.save(f);
        }
    }

    @Transactional   // 🔥 THIS IS THE FIX
    public void unfollow(Long userId, Long targetId) {
        repo.deleteByFollowerIdAndFollowingId(userId, targetId);
    }

    public boolean isFollowing(Long userId, Long targetId) {
        return repo.existsByFollowerIdAndFollowingId(userId, targetId);
    }
}