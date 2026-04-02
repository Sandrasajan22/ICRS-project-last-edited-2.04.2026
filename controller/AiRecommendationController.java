package com.main.icrsbackend.controller;

import com.main.icrsbackend.service.feed.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AiRecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Proxies the dashboard request securely through the Java Backend.
     */
    @PostMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@RequestBody Map<String, Object> requestPayload) {
        Long userId = Long.valueOf(requestPayload.getOrDefault("user_id", 0).toString());
        Map<String, Object> extra = (Map<String, Object>) requestPayload.get("user_data");

        Map<String, Object> result = recommendationService.getFullDashboard(userId, extra);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{userId}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardByPath(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, Object> extraDetails) {

        Map<String, Object> result = recommendationService.getFullDashboard(userId, extraDetails);
        return ResponseEntity.ok(result);
    }
}
