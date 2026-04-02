package com.main.icrsbackend.controller;

import com.main.icrsbackend.dto.ChangePasswordRequest;
import com.main.icrsbackend.model.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserSecurityController {

    private final UserSecurityService userSecurityService;

    // ✅ PUT /api/user/change-password
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userSecurityService.changePassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }

    // ✅ POST /api/user/logout-all/{userId}
    @PostMapping("/logout-all/{userId}")
    public ResponseEntity<?> logoutAll(@PathVariable Long userId) {
        userSecurityService.logoutAll(userId);
        return ResponseEntity.ok("Logged out from all devices");
    }

    // ✅ DELETE /api/user/{userId}
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long userId) {
        userSecurityService.deleteAccount(userId);
        return ResponseEntity.ok("Account deleted successfully");
    }
}