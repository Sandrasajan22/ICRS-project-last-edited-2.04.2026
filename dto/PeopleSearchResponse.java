package com.main.icrsbackend.dto;

public class PeopleSearchResponse {

    private Long id;
    private String name;

    private String email;
    private String role;
    private boolean verified;
    private String profileImage;

    public PeopleSearchResponse(Long id, String name, String email, String role, boolean verified, String profileImage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.verified = verified;
        this.profileImage = profileImage;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isVerified() { return verified; }
    public String getProfileImage() { return profileImage; }

    // getters
}