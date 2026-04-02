package com.main.icrsbackend.model.profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.icrsbackend.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "institution_profiles")
@Getter
@Setter
public class InstitutionProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Column(length = 255)
    private String institutionName;

    @Column(length = 255)
    private String institutionType;

    @Column(length = 255)
    private String website;

    @Column(length = 255)
    private String location;

    @Column(length = 255)
    private String contactPerson;

    @Column(length = 255)
    private String designation;

    @Column(length = 2000)
    private String about;

    @Column(length = 500)
    private String offeredPrograms;

    @Column(length = 500)
    private String certifications;

    @Column(length = 255)
    private String logo;
}