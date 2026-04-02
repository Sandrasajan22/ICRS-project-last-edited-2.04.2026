package com.main.icrsbackend.controller.jobseeker;

import com.main.icrsbackend.dto.academics.AcademicsRequest;
import com.main.icrsbackend.service.AcademicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/academics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AcademicsController {

    private final AcademicsService academicsService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAcademics(@PathVariable Long userId) {
        return ResponseEntity.ok(academicsService.getAcademics(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateAcademics(
            @PathVariable Long userId,
            @RequestBody AcademicsRequest request
    ) {
        return ResponseEntity.ok(academicsService.updateAcademics(userId, request));
    }

    @PostMapping("/{userId}/certificates")
    public ResponseEntity<?> uploadCertificate(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("index") Integer index
    ) {
        return ResponseEntity.ok(academicsService.uploadCertificate(userId, file, index));
    }
}