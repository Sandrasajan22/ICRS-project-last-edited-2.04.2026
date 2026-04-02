package com.main.icrsbackend.controller;

import com.main.icrsbackend.model.Complaint;
import com.main.icrsbackend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:5173")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(
            @RequestParam("userId") Long userId,
            @RequestParam("subject") String subject,
            @RequestParam("description") String description,
            @RequestParam(value = "screenshot", required = false) MultipartFile screenshot
    ) {
        try {
            // Notification is fired inside ComplaintService.submitComplaint()
            Complaint saved = complaintService.submitComplaint(userId, subject, description, screenshot);
            return ResponseEntity.ok(Map.of("message", "Complaint registered successfully", "id", saved.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPending() {
        List<Map<String, Object>> complaints = complaintService.getAllPending().stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("subject", c.getSubject());
            map.put("description", c.getDescription());
            map.put("status", c.getStatus());
            map.put("screenshotPath", c.getScreenshotPath());
            map.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
            if (c.getReporter() != null) {
                map.put("reporter", Map.of(
                        "id", c.getReporter().getId(),
                        "email", c.getReporter().getEmail(),
                        "name", (c.getReporter().getFname() != null ? c.getReporter().getFname() : "") + " " + (c.getReporter().getLname() != null ? c.getReporter().getLname() : "")
                ));
            }
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(complaintService.getResolvedForUser(userId));
    }

    @PutMapping("/resolve/{id}")
    public ResponseEntity<?> resolve(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String note = body.get("resolutionNote");
            // Notification is fired inside ComplaintService.resolveComplaint()
            complaintService.resolveComplaint(id, note);
            return ResponseEntity.ok(Map.of("message", "Complaint resolved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
