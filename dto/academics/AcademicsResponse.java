package com.main.icrsbackend.dto.academics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AcademicsResponse {
    private Long userId;
    private String degree;
    private String field;
    private String college;
    private String university;
    private String year;
    private String marks;
    private List<CertificationResponse> certifications;
}