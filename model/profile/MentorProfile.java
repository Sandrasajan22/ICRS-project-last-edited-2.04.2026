package com.main.icrsbackend.model.profile;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.icrsbackend.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mentor_profiles")
@Getter
@Setter
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Column(length = 150)
    private String headline;

    @Column(length = 100)
    private String specialization; // HR / Technical / Communication

    @Column(length = 2000)
    private String bio;

    @Column(length = 500)
    private String skills; // comma-separated

    @Column(length = 255)
    private String experience;

    @Column(length = 255)
    private String currentOrganization;

    @Column(length = 255)
    private String designation;

    @Column(length = 255)
    private String linkedinUrl;

    @Column(length = 255)
    private String profileImage;
}