package com.main.icrsbackend.service;

import com.main.icrsbackend.model.User;
import com.main.icrsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void signup(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ AUTO APPROVE RULE
        if (
                user.getRole().equals("student") ||
                        user.getRole().equals("job_seeker") ||
                        user.getRole().equals("admin")
        ) {
            user.setVerified(true);
            user.setVerificationStatus("APPROVED");
        } else {
            user.setVerified(false);
            user.setVerificationStatus("PENDING");
        }

        userRepository.save(user);
    }
}
