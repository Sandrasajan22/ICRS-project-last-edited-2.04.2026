package com.main.icrsbackend.repository.interview;

import com.main.icrsbackend.model.interview.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<InterviewFeedback, Long> {

    // Find feedback for a specific booking
    Optional<InterviewFeedback> findByBooking_Id(Long bookingId);

    // Find all feedback for a specific student
    List<InterviewFeedback> findByStudent_Id(Long studentId);

    // Find all feedback given by a specific mentor
    List<InterviewFeedback> findByMentor_Id(Long mentorId);
}
