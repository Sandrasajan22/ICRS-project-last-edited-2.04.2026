package com.main.icrsbackend.model.interview;

import com.main.icrsbackend.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "mentor_availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ FIX: Use relation instead of mentorId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // duration per slot (in minutes)
    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Integer maxSlots;

    // optional (calculated dynamically, not required in DB)
    private Integer bookedCount;

    @Column(nullable = false)
    private Double fee;

    @Column(nullable = false)
    private String interviewType;

    // ✅ Active flag for soft delete
    @Column(nullable = false)
    private boolean active = true;
}