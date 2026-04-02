package com.main.icrsbackend.controller;

import com.main.icrsbackend.dto.ProfileResponse;
import com.main.icrsbackend.service.PublicProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PublicProfileController {

    private final PublicProfileService service;

    @GetMapping("/{userId}")
    public ProfileResponse getProfile(@PathVariable Long userId) {
        return service.getProfile(userId);
    }
}