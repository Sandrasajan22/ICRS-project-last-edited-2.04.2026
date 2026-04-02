package com.main.icrsbackend.controller;

import com.main.icrsbackend.model.User;
import com.main.icrsbackend.repository.FollowRepository;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService service;
    private final FollowRepository repo;
    private final UserRepository userRepo;

    @PostMapping("/{targetId}")
    public void follow(@PathVariable Long targetId,
                       @RequestParam Long userId) {
        service.follow(userId, targetId);
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<?> unfollow(
            @PathVariable("targetId") Long targetId,
            @RequestParam("userId") Long userId) {

        service.unfollow(userId, targetId);  // ✅ FIXED

        return ResponseEntity.ok().build();
    }

    // followers
    @GetMapping("/followers/{id}")
    public List<User> followers(@PathVariable Long id) {
        return repo.findByFollowingId(id).stream()
                .map(f -> userRepo.findById(f.getFollowerId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    // following
    @GetMapping("/following/{id}")
    public List<User> following(@PathVariable Long id) {
        return repo.findByFollowerId(id).stream()
                .map(f -> userRepo.findById(f.getFollowingId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }
}