package com.main.icrsbackend.controller.student;

import com.main.icrsbackend.dto.StudentProfileDTO;
import com.main.icrsbackend.dto.StudentProfileUpdateDTO;
import com.main.icrsbackend.service.StudentProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/student/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentProfileController {

    private final StudentProfileService service;

    public StudentProfileController(StudentProfileService service) {
        this.service = service;
    }

    // GET /api/student/profile/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<StudentProfileDTO> get(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getProfile(userId));
    }

    // PUT /api/student/profile/{userId}
    @PutMapping("/{userId}")
    public ResponseEntity<StudentProfileDTO> update(
            @PathVariable Long userId,
            @RequestBody StudentProfileUpdateDTO body
    ) {
        return ResponseEntity.ok(service.updateProfile(userId, body));
    }

    // POST /api/student/profile/{userId}/image
    @PostMapping("/{userId}/image")
    public ResponseEntity<StudentProfileDTO> upload(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(service.uploadImage(userId, file));
    }
}