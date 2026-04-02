package com.main.icrsbackend.dto.interview;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.main.icrsbackend.model.interview.InterviewType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MentorAvailabilityResponse {
    private Long id;
    private Long mentorId;
    private String mentorName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private Integer duration;
    private InterviewType interviewType;
    private BigDecimal fee;
    private Integer maxSlots;
    private boolean active;
}
