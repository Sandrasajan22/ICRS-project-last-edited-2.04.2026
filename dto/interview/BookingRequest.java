package com.main.icrsbackend.dto.interview;

import lombok.Data;

import java.time.LocalTime;

@Data
public class BookingRequest {
    private Long mentorId;
    private Long candidateId;
    private Long availabilityId;
    private LocalTime time;
}
