package com.main.icrsbackend.controller.interview;

import com.main.icrsbackend.dto.interview.MentorAvailabilityRequest;
import com.main.icrsbackend.dto.interview.MentorAvailabilityResponse;
import com.main.icrsbackend.dto.interview.SlotResponse;
import com.main.icrsbackend.service.interview.MentorAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;


@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MentorAvailabilityController {

    private final MentorAvailabilityService service;

    @PostMapping("/availability")
    public MentorAvailabilityResponse create(@RequestBody MentorAvailabilityRequest req) {
        return service.create(req);
    }

    @GetMapping("/mentor/availability")
    public List<MentorAvailabilityResponse> mentor(@RequestParam Long mentorId) {
        return service.getMentorAvailability(mentorId);
    }

    @GetMapping("/student/availability")
    public List<SlotResponse> student(@RequestParam Long studentId) {
        return service.getAvailabilityForStudent(studentId);
    }
}