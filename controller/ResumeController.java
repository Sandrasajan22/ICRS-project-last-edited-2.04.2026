package com.main.icrsbackend.controller;

import com.main.icrsbackend.model.resume.Resume;
import com.main.icrsbackend.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/save")
    public ResponseEntity<?> saveResume(@RequestBody Resume resume) {
        return ResponseEntity.ok(resumeService.saveOrUpdate(resume));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getResumeByUserId(@PathVariable Long userId) {
        Resume resume = resumeService.getByUserId(userId);
        return ResponseEntity.ok(resume);
    }
}