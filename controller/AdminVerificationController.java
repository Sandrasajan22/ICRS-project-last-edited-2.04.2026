package com.main.icrsbackend.controller;

import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.VerificationRequest;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.VerificationRequestRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/admin/verification")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AdminVerificationController {

    private final VerificationRequestRepository verificationRepo;
    private final UserRepository userRepo;

    // ================= GET ALL PENDING =================
    @GetMapping("/pending")
    public ResponseEntity<List<VerificationRequest>> getPendingRequests() {

        List<VerificationRequest> list = verificationRepo.findByStatus("PENDING");

        if (list.isEmpty()) {
            return ResponseEntity.ok(List.of()); // empty array (safe for frontend)
        }

        return ResponseEntity.ok(list);
    }

    // ================= APPROVE =================
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {

        VerificationRequest req = verificationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if ("APPROVED".equalsIgnoreCase(req.getStatus())) {
            return ResponseEntity.badRequest().body("Already approved");
        }

        req.setStatus("APPROVED");
        req.setRemarks(null);
        verificationRepo.save(req);

        User user = req.getUser();
        user.setVerificationStatus("APPROVED");
        user.setVerified(true);
        user.setBlocked(false);
        userRepo.save(user);

        return ResponseEntity.ok("Approved successfully");
    }

    // ================= REJECT =================
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks
    ) {

        VerificationRequest req = verificationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if ("REJECTED".equalsIgnoreCase(req.getStatus())) {
            return ResponseEntity.badRequest().body("Already rejected");
        }

        req.setStatus("REJECTED");
        req.setRemarks(remarks != null ? remarks : "Rejected by admin");
        verificationRepo.save(req);

        User user = req.getUser();
        user.setVerificationStatus("REJECTED");
        user.setVerified(false);
        user.setBlocked(true);
        userRepo.save(user);

        return ResponseEntity.ok("Rejected successfully");
    }

    // ================= COUNT =================
    @GetMapping("/count")
    public ResponseEntity<Long> getPendingCount() {
        long count = verificationRepo.countByStatus("PENDING");
        return ResponseEntity.ok(count);
    }
}