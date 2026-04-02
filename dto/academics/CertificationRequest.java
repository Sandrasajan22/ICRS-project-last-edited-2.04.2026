package com.main.icrsbackend.dto.academics;

import lombok.Data;

@Data
public class CertificationRequest {
    private String title;
    private String issuer;
    private String year;
    private String description;
    private String imageUrl;
}