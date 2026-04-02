package com.main.icrsbackend.repository.interview;

import com.main.icrsbackend.model.interview.InterviewBooking;
import com.main.icrsbackend.model.interview.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface InterviewBookingRepository extends JpaRepository<InterviewBooking, Long> {

    List<InterviewBooking> findByStudent_Id(Long studentId);

    List<InterviewBooking> findByMentor_Id(Long mentorId);

    long countByAvailability_IdAndDateAndTimeAndStatusIn(
            Long availabilityId,
            LocalDate date,
            LocalTime time,
            Set<BookingStatus> statuses
    );

    boolean existsByStudent_IdAndAvailability_IdAndTimeAndStatusIn(
            Long studentId,
            Long availabilityId,
            LocalTime time,
            Set<BookingStatus> statuses
    );
}