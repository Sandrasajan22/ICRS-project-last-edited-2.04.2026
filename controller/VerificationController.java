package com.main.icrsbackend.controller;

import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.VerificationRequest;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.VerificationRequestRepository;
import com.main.icrsbackend.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "http://localhost:5173")
public class VerificationController {

    @Autowired
    private VerificationRequestRepository verificationRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotificationService notificationService;

    // ✅ Always save in PROJECT_ROOT/uploads/verification/
    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + File.separator +
                    "uploads" + File.separator + "verification" + File.separator;

    // ✅ Public URL for frontend/admin preview
    private static final String PUBLIC_URL_PREFIX = "/uploads/verification/";

    // ================= SUBMIT VERIFICATION =================
    @PostMapping("/submit")
    public ResponseEntity<?> submitVerification(
            @RequestParam("userId") Long userId,
            @RequestParam("idProof") MultipartFile idProof,
            @RequestParam("certificate") MultipartFile certificate,
            @RequestParam(value = "otherProof", required = false) MultipartFile otherProof
    ) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId is required"));
            }

            if (idProof == null || idProof.isEmpty() || certificate == null || certificate.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID Proof and Certificate are required"));
            }

            createUploadFolder();

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ❌ prevent re-submit if already approved
            if ("APPROVED".equalsIgnoreCase(user.getVerificationStatus())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Verification already approved"));
            }

            VerificationRequest request = verificationRepo
                    .findByUser_Id(userId)
                    .orElse(new VerificationRequest());

            // ✅ avoid duplicate notification if it is already pending in DB
            boolean wasExistingPending = request.getId() != null
                    && "PENDING".equalsIgnoreCase(request.getStatus());

            request.setUser(user);
            request.setStatus("PENDING");

            request.setIdProofPath(saveFile(idProof, "ID_PROOF"));
            request.setCertificatePath(saveFile(certificate, "CERTIFICATE"));

            if (otherProof != null && !otherProof.isEmpty()) {
                request.setOtherProofPath(saveFile(otherProof, "OTHER"));
            }

            // ✅ SAVE and capture the saved entity (important for id)
            VerificationRequest saved = verificationRepo.save(request);

            // ✅ CREATE NOTIFICATION (PLACE HERE)
            // only if it wasn't already an existing pending request
            if (!wasExistingPending) {
                notificationService.create(
                        0L, // Admin notification
                        "New verification request from " + user.getFname() + " " + user.getLname(),
                        "VERIFICATION",
                        "/admin/verification",
                        saved.getId()
                );
            }

            user.setVerificationStatus("PENDING");
            userRepo.save(user);

            return ResponseEntity.ok(Map.of("message", "submitted", "status", "PENDING"));

        } catch (Exception e) {
            e.printStackTrace();
            // ✅ return 500 for server issues
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ================= GET STATUS =================
    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getStatus(@PathVariable Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ auto-approved roles
        String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
        if (role.equals("student") || role.equals("job_seeker") || role.equals("admin")) {
            if (!"APPROVED".equalsIgnoreCase(user.getVerificationStatus())) {
                user.setVerificationStatus("APPROVED");
                user.setVerified(true);
                userRepo.save(user);
            }
            return ResponseEntity.ok(Map.of("status", "APPROVED"));
        }

        // ✅ source of truth: verification_requests table
        var reqOpt = verificationRepo.findByUser_Id(userId);

        if (reqOpt.isEmpty()) {
            if (!"NOT_SUBMITTED".equalsIgnoreCase(user.getVerificationStatus())) {
                user.setVerificationStatus("NOT_SUBMITTED");
                userRepo.save(user);
            }
            return ResponseEntity.ok(Map.of("status", "NOT_SUBMITTED"));
        }

        VerificationRequest req = reqOpt.get();
        String status = (req.getStatus() == null || req.getStatus().isBlank())
                ? "PENDING"
                : req.getStatus().toUpperCase();

        if (!status.equalsIgnoreCase(user.getVerificationStatus())) {
            user.setVerificationStatus(status);
            userRepo.save(user);
        }

        return ResponseEntity.ok(Map.of("status", status));
    }

    // ================= UTIL =================
    private void createUploadFolder() {
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) folder.mkdirs();
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {

        String original = (file.getOriginalFilename() == null)
                ? "file"
                : file.getOriginalFilename().replaceAll("[\\\\/:*?\"<>|]", "_");

        String fileName = prefix + "_" + UUID.randomUUID() + "_" + original;

        File destination = new File(UPLOAD_DIR + fileName);
        file.transferTo(destination);

        // ✅ store public path (frontend can use directly)
        return PUBLIC_URL_PREFIX + fileName;
    }
}
