package com.main.icrsbackend.controller.trainer;

import com.main.icrsbackend.model.trainer.Trainercertifications;
import com.main.icrsbackend.repository.trainer.Certificationsrepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/trainer/certifications")
@CrossOrigin(origins = "http://localhost:5173")
public class Certificationcontro {

    @Autowired
    private Certificationsrepo repo; // ✅ repo, not controller

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir")
                    + File.separator + "uploads"
                    + File.separator + "certifications"
                    + File.separator;

    // ✅ GET list
    // /api/trainer/certifications?trainerId=9
    @GetMapping
    public ResponseEntity<?> list(@RequestParam Long trainerId) {
        return ResponseEntity.ok(repo.findByTrainerIdOrderByCreatedAtDesc(trainerId));
    }

    // ✅ POST create with file upload
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> create(
            @RequestParam Long trainerId,
            @RequestParam String title,
            @RequestParam(required = false) String issuer,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) MultipartFile file
    ) {
        Trainercertifications c = new Trainercertifications();
        c.setTrainerId(trainerId);
        c.setTitle(title);
        c.setIssuer(issuer);
        c.setYear(year);

        if (file != null && !file.isEmpty()) {
            c.setFileUrl(saveFile(trainerId, file));
        }

        return ResponseEntity.ok(repo.save(c));
    }

    // ✅ PUT update (optionally replace file)
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String issuer,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) MultipartFile file
    ) {
        Trainercertifications c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found: " + id));

        if (title != null) c.setTitle(title);
        if (issuer != null) c.setIssuer(issuer);
        if (year != null) c.setYear(year);

        if (file != null && !file.isEmpty()) {
            deleteFileIfExists(c.getFileUrl());
            c.setFileUrl(saveFile(c.getTrainerId(), file));
        }

        return ResponseEntity.ok(repo.save(c));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Trainercertifications c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found: " + id));

        deleteFileIfExists(c.getFileUrl());
        repo.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    // ---------------- helpers ----------------

    private String saveFile(Long trainerId, MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if (ext == null || ext.isBlank()) ext = "bin";

            String lower = ext.toLowerCase();
            List<String> allowed = Arrays.asList("pdf", "jpg", "jpeg", "png", "webp");
            if (!allowed.contains(lower)) {
                throw new RuntimeException("Only PDF/JPG/PNG/WEBP allowed");
            }

            String name = "cert_" + trainerId + "_" + UUID.randomUUID() + "." + lower;
            Path path = Paths.get(UPLOAD_DIR + name);
            Files.write(path, file.getBytes());

            return "/uploads/certifications/" + name;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    private void deleteFileIfExists(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isBlank()) return;

            // "/uploads/certifications/x.pdf" -> "uploads/certifications/x.pdf"
            String relative = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;

            Path p = Paths.get(System.getProperty("user.dir"), relative);
            Files.deleteIfExists(p);
        } catch (Exception ignored) {}
    }
}