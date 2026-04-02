package com.main.icrsbackend.repository.interview;

import com.main.icrsbackend.model.interview.MentorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MentorAvailabilityRepository extends JpaRepository<MentorAvailability, Long> {

    List<MentorAvailability> findByMentor_IdAndActiveTrueOrderByDateAscStartTimeAsc(Long mentorId);

    List<MentorAvailability> findByActiveTrueAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate date);
}