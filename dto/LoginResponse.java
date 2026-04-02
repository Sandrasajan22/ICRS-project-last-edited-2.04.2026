package com.main.icrsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String fname;
    private String lname;
    private String email;
    private String role;

    private boolean blocked;
    private String verificationStatus;

    // ✅ ADD THIS
    private String token;
}