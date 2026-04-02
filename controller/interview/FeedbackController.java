package com.main.icrsbackend.controller.interview;

import com.main.icrsbackend.model.interview.InterviewFeedback;
import com.main.icrsbackend.service.interview.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews/feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FeedbackController {

    private final FeedbackService feedbackService;

    // ================= SUBMIT =================
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody InterviewFeedback feedback) {
        try {
            return ResponseEntity.ok(feedbackService.submitFeedback(feedback));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ================= STUDENT PERFORMANCE =================
    @GetMapping("/student/{studentId}")
    public List<InterviewFeedback> getStudentPerformance(@PathVariable Long studentId) {
        return feedbackService.getStudentPerformance(studentId);
    }

    // ================= BOOKING FEEDBACK =================
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getBookingFeedback(@PathVariable Long bookingId) {
        InterviewFeedback feedback = feedbackService.getBookingFeedback(bookingId);
        if (feedback == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feedback);
    }
}
