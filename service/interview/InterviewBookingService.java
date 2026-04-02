package com.main.icrsbackend.service.interview;

import com.main.icrsbackend.dto.interview.BookingResponse;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.interview.BookingStatus;
import com.main.icrsbackend.model.interview.InterviewBooking;
import com.main.icrsbackend.model.interview.MentorAvailability;
import com.main.icrsbackend.model.interview.PaymentStatus;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.interview.InterviewBookingRepository;
import com.main.icrsbackend.repository.interview.MentorAvailabilityRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewBookingService {

    private final InterviewBookingRepository repo;
    private final MentorAvailabilityRepository availabilityRepo;
    private final UserRepository userRepo;

    // ================= BOOK =================
    public BookingResponse book(Long studentId, Long availabilityId, String timeStr) {

        User student = userRepo.findById(studentId).orElseThrow();
        MentorAvailability a = availabilityRepo.findById(availabilityId).orElseThrow();

        LocalTime time = LocalTime.parse(timeStr);

        long count = repo.countByAvailability_IdAndDateAndTimeAndStatusIn(
                availabilityId,
                a.getDate(),
                time,
                EnumSet.of(BookingStatus.BOOKED)
        );

        if (count >= a.getMaxSlots()) {
            throw new RuntimeException("Slot full");
        }

        InterviewBooking b = InterviewBooking.builder()
                .student(student)
                .mentor(a.getMentor())
                .availability(a)
                .date(a.getDate())
                .time(time)
                .status(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        return map(repo.save(b));
    }

    // ================= PAY =================
    public BookingResponse pay(Long id) {

        InterviewBooking b = repo.findById(id).orElseThrow();

        b.setPaymentStatus(PaymentStatus.PAID);
        b.setMeetingLink("https://meet.jit.si/interview-" + b.getId());

        return map(repo.save(b));
    }

    // ================= STUDENT BOOKINGS =================
    public List<BookingResponse> studentBookings(Long studentId) {
        return repo.findByStudent_Id(studentId)
                .stream()
                .map(this::map)
                .toList();
    }

    // ================= MENTOR BOOKINGS =================
    public List<BookingResponse> mentorBookings(Long mentorId) {
        return repo.findByMentor_Id(mentorId)
                .stream()
                .map(this::map)
                .toList();
    }

    // ================= SHARE =================
    public String share(Long id) {

        InterviewBooking b = repo.findById(id).orElseThrow();

        if (b.getPaymentStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Payment not completed");
        }

        return b.getMeetingLink();
    }

    // ================= MAPPER =================
    private BookingResponse map(InterviewBooking b) {
        return BookingResponse.builder()
                .id(b.getId())

                // mentor
                .mentorId(b.getMentor().getId())
                .mentorName(
                        b.getMentor().getFname() + " " + b.getMentor().getLname()
                )

                // student
                .studentId(b.getStudent().getId())
                .studentName(
                        b.getStudent().getFname() + " " + b.getStudent().getLname()
                )

                // availability
                .availabilityId(b.getAvailability().getId())

                // booking details
                .date(b.getDate())
                .time(b.getTime())
                .fee(BigDecimal.valueOf(b.getAvailability().getFee()))


                // 🔥 NEW FIELDS
                .interviewType(
                        b.getAvailability() != null && b.getAvailability().getInterviewType() != null
                                ? b.getAvailability().getInterviewType()
                                : "N/A"
                )
                .paymentAmount(
                        BigDecimal.valueOf(b.getAvailability().getFee())
                )
                // status
                .status(b.getStatus())
                .paymentStatus(b.getPaymentStatus())

                // meeting
                .meetingLink(b.getMeetingLink())

                .build();
    }
}