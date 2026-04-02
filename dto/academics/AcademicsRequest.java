package com.main.icrsbackend.dto.academics;

import lombok.Data;

import java.util.List;

@Data
public class AcademicsRequest {
    private String degree;
    private String field;
    private String college;
    private String university;
    private String year;
    private String marks;
    private List<CertificationRequest> certifications;
}