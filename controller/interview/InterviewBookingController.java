package com.main.icrsbackend.controller.interview;

import com.main.icrsbackend.dto.interview.BookingResponse;
import com.main.icrsbackend.service.interview.InterviewBookingService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // ✅ better than "*"
public class InterviewBookingController {

    private final InterviewBookingService service;

    // ================= BOOK =================
    @PostMapping("/book")
    public BookingResponse book(
            @RequestParam Long studentId,
            @RequestParam Long availabilityId,
            @RequestParam String time
    ) {
        return service.book(studentId, availabilityId, time);
    }

    // ================= PAY =================
    @PutMapping("/pay/{id}")
    public BookingResponse pay(@PathVariable Long id) {
        return service.pay(id);
    }

    // ================= STUDENT BOOKINGS =================
    @GetMapping("/student/bookings")
    public List<BookingResponse> student(@RequestParam Long studentId) {
        return service.studentBookings(studentId);
    }

    // ================= MENTOR BOOKINGS =================
    @GetMapping("/mentor/bookings")
    public List<BookingResponse> mentor(@RequestParam Long mentorId) {
        return service.mentorBookings(mentorId);
    }

    // ================= SHARE =================
    @GetMapping("/share/{id}")
    public String share(@PathVariable Long id) {
        return service.share(id); // ✅ already returns link
    }
}