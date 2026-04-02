package com.main.icrsbackend.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.icrsbackend.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employer_profiles")
@Getter
@Setter
public class EmployerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Column(length = 255)
    private String companyName;

    @Column(length = 255)
    private String companyWebsite;

    @Column(length = 255)
    private String industry;

    @Column(length = 255)
    private String companySize;

    @Column(length = 255)
    private String headquarters;

    @Column(length = 255)
    private String hrName;

    @Column(length = 255)
    private String designation;

    @Column(length = 2000)
    private String companyDescription;

    @Column(length = 500)
    private String hiringRoles; // comma-separated

    @Column(length = 255)
    private String linkedinUrl;

    @Column(length = 255)
    private String logo;

    @Column(length = 1000)
    private String additionalSkills;
}