package com.main.icrsbackend.controller.profilecontroller;

import com.main.icrsbackend.dto.profiledto.InstitutionProfileDTO;
import com.main.icrsbackend.service.profile.InstitutionProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/institution-profile")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class InstitutionProfileController {

    private final InstitutionProfileService institutionProfileService;

    // GET /api/institution-profile/users/{userId}
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        log.info("GET /api/institution-profile/users/{} called", userId);
        try {
            InstitutionProfileDTO dto = institutionProfileService.getByUserId(userId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException iae) {
            log.info("Institution/profile not found for id {}: {}", userId, iae.getMessage());
            return ResponseEntity.status(404).body(java.util.Map.of("error", iae.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException ade) {
            log.warn("Access denied for institution {}: {}", userId, ade.getMessage());
            return ResponseEntity.status(403).body(java.util.Map.of("error", "Access denied for this role"));
        } catch (Exception ex) {
            log.error("Error fetching institution profile for {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Internal server error: " + ex.getMessage()));
        }
    }

    // PUT /api/institution-profile/{userId}
    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestPart("data") String data,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) {
        log.info("PUT /api/institution-profile/{} called", userId);
        try {
            // If your service expects DTO directly from @RequestPart, adapt accordingly.
            // Here we assume JSON string is provided similar to Mentor controller.
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            InstitutionProfileDTO dto = mapper.readValue(data, InstitutionProfileDTO.class);
            InstitutionProfileDTO updated = institutionProfileService.update(userId, dto, logo);
            return ResponseEntity.ok(updated);
        } catch (com.fasterxml.jackson.core.JsonProcessingException jex) {
            log.warn("Invalid JSON for institution {}: {}", userId, jex.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid profile data"));
        } catch (IllegalArgumentException iae) {
            log.warn("Validation or access error updating institution {}: {}", userId, iae.getMessage());
            return ResponseEntity.status(400).body(java.util.Map.of("error", iae.getMessage()));
        } catch (Exception ex) {
            log.error("Failed to update institution profile for user {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to update profile"));
        }
    }
}
