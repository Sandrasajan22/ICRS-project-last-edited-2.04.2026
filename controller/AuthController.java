package com.main.icrsbackend.controller;

import com.main.icrsbackend.model.User;
import com.main.icrsbackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ================= SIGNUP =================
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // 🔐 Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String role = user.getRole() == null ? "" : user.getRole().toLowerCase();

        // 🔐 VERIFICATION LOGIC (UNCHANGED LOGIC)
        if (role.equals("student") ||
                role.equals("job_seeker") ||
                role.equals("admin")) {

            user.setVerified(true);
            user.setVerificationStatus("APPROVED");

        } else {
            user.setVerified(false);
            user.setVerificationStatus("NOT_SUBMITTED");
        }

        userRepository.save(user);

        return ResponseEntity.ok(
                "Signup successful. Verification status: " + user.getVerificationStatus()
        );
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByEmail(email).orElse(null);

        // ❌ INVALID LOGIN
        if (user == null ||
                !passwordEncoder.matches(password, user.getPassword())) {

            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.ok(response);
        }

        // 🚫 BLOCKED ACCOUNT
        if (user.isBlocked()) {
            response.put("success", false);
            response.put("message", "This account has been deactivated. Please contact support.");
            return ResponseEntity.ok(response);
        }

        // ⏳ PENDING VERIFICATION (NO LOGIC CHANGE)
        if (!user.isVerified()) {

            response.put("success", true);
            response.put("message", "Account pending admin verification");
            response.put("verificationStatus", user.getVerificationStatus());

            response.put("userId", user.getId());
            response.put("role", user.getRole());

            // ✅ ADDED NAME FIELDS
            response.put("fname", user.getFname());
            response.put("lname", user.getLname());
            response.put("fullName",
                    ((user.getFname() == null ? "" : user.getFname()) + " " +
                            (user.getLname() == null ? "" : user.getLname())).trim()
            );

            return ResponseEntity.ok(response);
        }

        // ✅ LOGIN SUCCESS (ONLY NAME ADDED)
        response.put("success", true);
        response.put("userId", user.getId());
        response.put("role", user.getRole());
        response.put("email", user.getEmail());
        response.put("verificationStatus", user.getVerificationStatus());

        // ✅ ADDED FOR SIDEBAR NAME
        response.put("fname", user.getFname());
        response.put("lname", user.getLname());
        response.put("fullName",
                ((user.getFname() == null ? "" : user.getFname()) + " " +
                        (user.getLname() == null ? "" : user.getLname())).trim()
        );

        return ResponseEntity.ok(response);
    }
}
