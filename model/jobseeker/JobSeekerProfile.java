package com.main.icrsbackend.model.jobseeker;

import com.main.icrsbackend.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "jobseeker_profiles",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // one profile per user
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String fname;
    private String lname;
    private String fullName;

    private String phone;
    private String location;
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // ✅ AI-ready normalized skills (many rows is correct)
    // ✅ prevent duplicates per profile with unique(profile_id, skill)
    @ElementCollection
    @CollectionTable(
            name = "jobseeker_profile_skills",
            joinColumns = @JoinColumn(name = "profile_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "skill"})
    )
    @Column(name = "skill", length = 60)
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    private String linkedin;
    private String github;
    private String portfolio;

    // relative: "/uploads/jobseeker/xyz.jpg"
    private String profilePhoto;
}