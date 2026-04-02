package com.main.icrsbackend.controller.profilecontroller;

import com.main.icrsbackend.dto.profiledto.EmployerProfileDTO;
import com.main.icrsbackend.service.profile.EmployerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employer-profile")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;

    // GET /api/employer-profile/users/{userId}
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        log.info("GET /api/employer-profile/users/{} called", userId);
        try {
            EmployerProfileDTO dto = employerProfileService.getByUserId(userId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException iae) {
            log.info("Employer/profile not found for id {}: {}", userId, iae.getMessage());
            return ResponseEntity.status(404).body(java.util.Map.of("error", iae.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException ade) {
            log.warn("Access denied for employer {}: {}", userId, ade.getMessage());
            return ResponseEntity.status(403).body(java.util.Map.of("error", "Access denied for this role"));
        } catch (Exception ex) {
            log.error("Error fetching employer profile for {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Internal server error: " + ex.getMessage()));
        }
    }

    // PUT /api/employer-profile/{userId}
    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestPart("data") String data,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) {
        log.info("PUT /api/employer-profile/{} called", userId);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            EmployerProfileDTO dto = mapper.readValue(data, EmployerProfileDTO.class);
            EmployerProfileDTO updated = employerProfileService.update(userId, dto, logo);
            return ResponseEntity.ok(updated);
        } catch (com.fasterxml.jackson.core.JsonProcessingException jex) {
            log.warn("Invalid JSON for employer {}: {}", userId, jex.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid profile data"));
        } catch (IllegalArgumentException iae) {
            log.warn("Validation or access error updating employer {}: {}", userId, iae.getMessage());
            return ResponseEntity.status(400).body(java.util.Map.of("error", iae.getMessage()));
        } catch (Exception ex) {
            log.error("Failed to update employer profile for user {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to update profile"));
        }
    }
}
