package com.main.icrsbackend.service.interview;

import com.main.icrsbackend.model.interview.BookingStatus;
import com.main.icrsbackend.model.interview.InterviewBooking;
import com.main.icrsbackend.model.interview.InterviewFeedback;
import com.main.icrsbackend.model.interview.PaymentStatus;
import com.main.icrsbackend.repository.interview.FeedbackRepository;
import com.main.icrsbackend.repository.interview.InterviewBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepo;
    private final InterviewBookingRepository bookingRepo;

    // ================= SUBMIT FEEDBACK =================
    public InterviewFeedback submitFeedback(InterviewFeedback feedback) {

        // 🏗️ 1. Find the booking
        if (feedback.getBooking() == null || feedback.getBooking().getId() == null) {
            throw new RuntimeException("Booking ID is required");
        }
        long bookingId = feedback.getBooking().getId();
        InterviewBooking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 🏗️ 2. Validate PAID & COMPLETED status
        if (booking.getPaymentStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Cannot review an unpaid interview.");
        }

        // Note: For now, we will mark it as COMPLETED when feedback is submitted
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepo.save(booking);

        // 🏗️ 3. Set student and mentor from booking
        feedback.setStudent(booking.getStudent());
        feedback.setMentor(booking.getMentor());

        return feedbackRepo.save(feedback);
    }

    // ================= GET REVIEWS FOR STUDENT =================
    public List<InterviewFeedback> getStudentPerformance(Long studentId) {
        return feedbackRepo.findByStudent_Id(studentId);
    }

    // ================= GET BOOKING FEEDBACK =================
    public InterviewFeedback getBookingFeedback(Long bookingId) {
        return feedbackRepo.findByBooking_Id(bookingId).orElse(null);
    }
}
