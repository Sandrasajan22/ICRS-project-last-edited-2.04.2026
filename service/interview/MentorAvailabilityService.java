package com.main.icrsbackend.service.interview;

import com.main.icrsbackend.dto.interview.*;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.interview.*;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.interview.InterviewBookingRepository;
import com.main.icrsbackend.repository.interview.MentorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MentorAvailabilityService {

    private final MentorAvailabilityRepository availabilityRepo;
    private final InterviewBookingRepository bookingRepo;
    private final UserRepository userRepo;

    // ================= CREATE =================
    public MentorAvailabilityResponse create(MentorAvailabilityRequest req) {

        User mentor = userRepo.findById(req.getMentorId())
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        MentorAvailability a = MentorAvailability.builder()
                .mentor(mentor)
                .date(req.getDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .duration(req.getDuration())
                .maxSlots(req.getMaxSlots())
                .fee(req.getFee().doubleValue())
                .interviewType(req.getInterviewType().name())
                .active(true)
                .build();

        return map(availabilityRepo.save(a));
    }

    // ================= UPDATE =================
    public MentorAvailabilityResponse update(Long id, MentorAvailabilityRequest req) {

        MentorAvailability a = availabilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!a.getMentor().getId().equals(req.getMentorId())) {
            throw new RuntimeException("Unauthorized");
        }

        a.setDate(req.getDate());
        a.setStartTime(req.getStartTime());
        a.setEndTime(req.getEndTime());
        a.setDuration(req.getDuration());
        a.setMaxSlots(req.getMaxSlots());
        a.setFee(req.getFee().doubleValue());
        a.setInterviewType(req.getInterviewType().name());

        return map(availabilityRepo.save(a));
    }

    // ================= DELETE =================
    public void delete(Long id, Long mentorId) {

        MentorAvailability a = availabilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!a.getMentor().getId().equals(mentorId)) {
            throw new RuntimeException("Unauthorized");
        }

        a.setActive(false); // soft delete
        availabilityRepo.save(a);
    }

    // ================= MENTOR VIEW =================
    public List<MentorAvailabilityResponse> getMentorAvailability(Long mentorId) {

        return availabilityRepo
                .findByMentor_IdAndActiveTrueOrderByDateAscStartTimeAsc(mentorId)
                .stream()
                .map(this::map)
                .toList();
    }

    // ================= STUDENT VIEW =================
    public List<SlotResponse> getAvailabilityForStudent(Long studentId) {

        return availabilityRepo
                .findByActiveTrueAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate.now())
                .stream()
                .flatMap(a -> expandSlots(a).stream())
                .toList();
    }

    // ================= SLOT EXPANSION =================
    private List<SlotResponse> expandSlots(MentorAvailability a) {

        List<SlotResponse> list = new ArrayList<>();

        LocalTime time = a.getStartTime();

        while (!time.plusMinutes(a.getDuration()).isAfter(a.getEndTime())) {

            long booked = bookingRepo.countByAvailability_IdAndDateAndTimeAndStatusIn(
                    a.getId(),
                    a.getDate(),
                    time,
                    EnumSet.of(BookingStatus.BOOKED)
            );

            list.add(SlotResponse.builder()
                    .availabilityId(a.getId())
                    .mentorId(a.getMentor().getId())
                    .mentorName(a.getMentor().getFname())
                    .date(a.getDate())
                    .startTime(time)
                    .endTime(time.plusMinutes(a.getDuration()))
                    .duration(a.getDuration())
                    .interviewType(InterviewType.valueOf(a.getInterviewType()))
                    .fee(BigDecimal.valueOf(a.getFee()))
                    .maxSlots(a.getMaxSlots())
                    .bookedCount((int) booked)
                    .available(booked < a.getMaxSlots())
                    .build());

            time = time.plusMinutes(a.getDuration());
        }

        return list;
    }

    // ================= MAPPER =================
    private MentorAvailabilityResponse map(MentorAvailability a) {
        return MentorAvailabilityResponse.builder()
                .id(a.getId())
                .mentorId(a.getMentor().getId())
                .mentorName(a.getMentor().getFname())
                .date(a.getDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .duration(a.getDuration())
                .interviewType(InterviewType.valueOf(a.getInterviewType()))
                .fee(BigDecimal.valueOf(a.getFee()))
                .maxSlots(a.getMaxSlots())
                .active(a.isActive())
                .build();
    }
}