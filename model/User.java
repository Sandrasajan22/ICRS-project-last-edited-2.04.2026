package com.main.icrsbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.main.icrsbackend.model.jobseeker.JobSeekerProfile;
import jakarta.persistence.*;
import com.main.icrsbackend.model.profile.MentorProfile;
import com.main.icrsbackend.model.profile.EmployerProfile;
import com.main.icrsbackend.model.profile.InstitutionProfile;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fname;
    private String lname;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private String role;

    // ================= ACCOUNT ACCESS =================
    @Column(nullable = false)
    private boolean blocked = false;

    // ================= VERIFICATION =================
    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(length = 20, nullable = false)
    private String verificationStatus = "NOT_SUBMITTED";

    // ================= TOKEN =================
    @Column(name = "token_version", nullable = false)
    private Integer tokenVersion = 0;

    // ================= TRACKING =================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // ================= RELATIONS =================
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonBackReference
    private VerificationRequest verificationRequest;

    // ✅ JobSeeker profile holds jobseeker details + photo
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JobSeekerProfile jobSeekerProfile;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MentorProfile mentorProfile;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private EmployerProfile employerProfile;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private InstitutionProfile institutionProfile;
}
