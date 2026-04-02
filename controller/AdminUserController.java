package com.main.icrsbackend.controller;

import com.main.icrsbackend.model.User;
import com.main.icrsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    // Fetch all users
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("fname", u.getFname());
            map.put("lname", u.getLname());
            map.put("email", u.getEmail());
            map.put("role", u.getRole());
            map.put("verified", u.isVerified());
            map.put("verificationStatus", u.getVerificationStatus());
            map.put("blocked", u.isBlocked());
            map.put("createdAt", u.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Toggle Block Status
    @PostMapping("/{userId}/toggle-block")
    public ResponseEntity<?> toggleBlock(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
        }

        User user = optionalUser.get();
        // Prevent admin from blocking themselves
        if ("admin".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot block an admin account."));
        }

        user.setBlocked(!user.isBlocked());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User " + (user.isBlocked() ? "blocked" : "unblocked") + " successfully",
                "blocked", user.isBlocked()
        ));
    }
}
