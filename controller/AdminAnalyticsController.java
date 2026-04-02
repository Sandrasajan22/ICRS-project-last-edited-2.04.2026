package com.main.icrsbackend.controller;

import com.main.icrsbackend.model.User;
import com.main.icrsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminAnalyticsController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        List<User> allUsers = userRepository.findAll();

        long totalUsers = allUsers.size();
        long blockedUsers = allUsers.stream().filter(User::isBlocked).count();
        long activeUsers = totalUsers - blockedUsers;
        long verifiedUsers = allUsers.stream().filter(User::isVerified).count();

        Map<String, Long> roleDistribution = new HashMap<>();
        Map<String, Long> monthlyRegistrations = new LinkedHashMap<>();

        // Initialize last 6 months (including current)
        LocalDateTime now = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthDate = now.minusMonths(i);
            String monthName = monthDate.getMonth().name().substring(0, 3); // JAN, FEB
            monthlyRegistrations.put(monthName, 0L);
        }

        for (User user : allUsers) {
            // Role Aggregation
            String role = user.getRole() != null ? user.getRole() : "UNKNOWN";
            if (!role.equalsIgnoreCase("admin")) {
                roleDistribution.put(role, roleDistribution.getOrDefault(role, 0L) + 1);
            }

            // Month Aggregation
            LocalDateTime createdAt = user.getCreatedAt();
            if (createdAt == null) {
                // Legacy user fallback
                createdAt = now;
            }

            // Only count if within last 6 months
            if (!createdAt.isBefore(now.minusMonths(5).withDayOfMonth(1).toLocalDate().atStartOfDay())) {
                String mName = createdAt.getMonth().name().substring(0, 3);
                monthlyRegistrations.put(mName, monthlyRegistrations.getOrDefault(mName, 0L) + 1);
            }
        }

        // Format for response DTO
        List<Map<String, Object>> roleData = new ArrayList<>();
        roleDistribution.forEach((key, value) -> {
            roleData.add(Map.of("name", key.toUpperCase().replace("_", " "), "value", value));
        });

        // Add colors for UI mapping later
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        monthlyRegistrations.forEach((key, value) -> {
            monthlyData.add(Map.of("name", key, "value", value));
        });

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", totalUsers);
        response.put("activeUsers", activeUsers);
        response.put("blockedUsers", blockedUsers);
        response.put("verifiedUsers", verifiedUsers);
        response.put("roleData", roleData);
        response.put("monthlyRegistration", monthlyData);

        return ResponseEntity.ok(response);
    }
}
