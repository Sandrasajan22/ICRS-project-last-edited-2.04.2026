package com.main.icrsbackend.controller.profilecontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.icrsbackend.dto.profiledto.MentorProfileDTO;
import com.main.icrsbackend.service.profile.MentorProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mentor-profile")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class MentorProfileController {

    private final MentorProfileService mentorProfileService;
    private final ObjectMapper objectMapper;

    /**
     * GET /api/mentor-profile/users/{id}
     * Returns MentorProfileDTO (combines user + profile fields)
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        log.info("GET /api/mentor-profile/users/{} called", id);
        try {
            MentorProfileDTO dto = mentorProfileService.getByUserId(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException iae) {
            log.info("User/profile not found or access denied for id {}: {}", id, iae.getMessage());
            return ResponseEntity.status(404).body(java.util.Map.of("error", iae.getMessage()));
        } catch (Exception ex) {
            log.error("Error fetching mentor profile for {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Internal server error"));
        }
    }

    /**
     * PUT /api/mentor-profile/{userId}
     * Consumes multipart/form-data with a "data" JSON part and optional "profileImage" file part.
     */
    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestPart("data") String data,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        log.info("PUT /api/mentor-profile/{} called", userId);

        try {
            MentorProfileDTO dto = objectMapper.readValue(data, MentorProfileDTO.class);
            MentorProfileDTO updated = mentorProfileService.update(userId, dto, profileImage);
            return ResponseEntity.ok(updated);
        } catch (com.fasterxml.jackson.core.JsonProcessingException jex) {
            log.warn("Invalid JSON for user {}: {}", userId, jex.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid profile data"));
        } catch (IllegalArgumentException iae) {
            log.warn("Validation or access error updating user {}: {}", userId, iae.getMessage());
            return ResponseEntity.status(400).body(java.util.Map.of("error", iae.getMessage()));
        } catch (IllegalStateException ise) {
            log.error("Storage error updating profile for user {}: {}", userId, ise.getMessage(), ise);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to store profile image"));
        } catch (Exception ex) {
            log.error("Failed to update profile for user {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to update profile"));
        }
    }
}
