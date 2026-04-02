package com.main.icrsbackend.controller.jobseeker;

import com.main.icrsbackend.dto.JobSeekerProfileDTO;
import com.main.icrsbackend.dto.JobSeekerProfileUpdateDTO;
import com.main.icrsbackend.service.JobSeekerProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/jobseeker/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class JobSeekerProfileController {

    private final JobSeekerProfileService service;

    public JobSeekerProfileController(JobSeekerProfileService service) {
        this.service = service;
    }

    // GET /api/jobseeker/profile/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<JobSeekerProfileDTO> get(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getProfile(userId));
    }

    // PUT /api/jobseeker/profile/{userId}
    @PutMapping("/{userId}")
    public ResponseEntity<JobSeekerProfileDTO> update(
            @PathVariable Long userId,
            @RequestBody JobSeekerProfileUpdateDTO body
    ) {
        return ResponseEntity.ok(service.updateProfile(userId, body));
    }

    // POST /api/jobseeker/profile/{userId}/image (multipart/form-data)
    @PostMapping("/{userId}/image")
    public ResponseEntity<JobSeekerProfileDTO> upload(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(service.uploadImage(userId, file));
    }
}