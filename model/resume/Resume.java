package com.main.icrsbackend.model.resume;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long userId;

    @Column(length = 50)
    private String selectedTemplate;

    @Column(length = 200)
    private String fullName;

    @Column(length = 200)
    private String role;

    @Column(length = 50)
    private String phone;

    @Column(length = 200)
    private String location;

    @Column(length = 200)
    private String email;

    @Column(length = 300)
    private String linkedin;

    @Column(length = 300)
    private String portfolio;

    @Column(length = 500)
    private String profilePhoto;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String objective;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String skillsJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String educationsJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String experiencesJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String languagesJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String certificationsJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String projectsJson;

    public Resume() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSelectedTemplate() {
        return selectedTemplate;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getObjective() {
        return objective;
    }

    public String getSkillsJson() {
        return skillsJson;
    }

    public String getEducationsJson() {
        return educationsJson;
    }

    public String getExperiencesJson() {
        return experiencesJson;
    }

    public String getLanguagesJson() {
        return languagesJson;
    }

    public String getCertificationsJson() {
        return certificationsJson;
    }

    public String getProjectsJson() {
        return projectsJson;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setSelectedTemplate(String selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setSkillsJson(String skillsJson) {
        this.skillsJson = skillsJson;
    }

    public void setEducationsJson(String educationsJson) {
        this.educationsJson = educationsJson;
    }

    public void setExperiencesJson(String experiencesJson) {
        this.experiencesJson = experiencesJson;
    }

    public void setLanguagesJson(String languagesJson) {
        this.languagesJson = languagesJson;
    }

    public void setCertificationsJson(String certificationsJson) {
        this.certificationsJson = certificationsJson;
    }

    public void setProjectsJson(String projectsJson) {
        this.projectsJson = projectsJson;
    }
}