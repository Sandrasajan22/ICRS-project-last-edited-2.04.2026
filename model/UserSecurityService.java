package com.main.icrsbackend.model;

import com.main.icrsbackend.dto.ChangePasswordRequest;
import com.main.icrsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // =========================
    // CHANGE PASSWORD
    // =========================
    public void changePassword(ChangePasswordRequest req) {

        if (req.getUserId() == null) throw new RuntimeException("UserId is required");
        if (req.getCurrentPassword() == null || req.getCurrentPassword().isBlank())
            throw new RuntimeException("Current password is required");
        if (req.getNewPassword() == null || req.getNewPassword().isBlank())
            throw new RuntimeException("New password is required");
        if (req.getNewPassword().length() < 8)
            throw new RuntimeException("New password must be at least 8 characters");

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // verify old password
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // prevent same password reuse
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password cannot be same as current password");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));

        // invalidate sessions/tokens after password change (recommended)
        user.setTokenVersion(user.getTokenVersion() + 1);

        userRepository.save(user);
    }

    // =========================
    // LOGOUT ALL DEVICES (JWT Best Practice)
    // =========================
    public void logoutAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // increase tokenVersion => all existing JWTs become invalid
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    // =========================
    // DEACTIVATE ACCOUNT (Soft-Delete)
    // =========================
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🛡️ Prevent deactivating parent accounts (Admin)
        if ("admin".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Parent account (Admin) cannot be deactivated.");
        }

        user.setBlocked(true);
        userRepository.save(user);
    }
}