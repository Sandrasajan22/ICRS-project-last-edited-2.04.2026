package com.main.icrsbackend.dto.academics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificationResponse {
    private Long id;
    private String title;
    private String issuer;
    private String year;
    private String description;
    private String imageUrl;
}