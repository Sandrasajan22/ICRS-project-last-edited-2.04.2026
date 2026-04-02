package com.main.icrsbackend.dto.interview;

import com.main.icrsbackend.model.interview.BookingStatus;
import com.main.icrsbackend.model.interview.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private Long mentorId;
    private String mentorName;
    private Long studentId;
    private String studentName;
    private Long availabilityId;
    private LocalDate date;
    private LocalTime time;
    private BigDecimal fee;
    private String interviewType;
    private BigDecimal paymentAmount;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String meetingLink;
}
