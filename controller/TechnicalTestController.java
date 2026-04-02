package com.main.icrsbackend.controller;

import com.main.icrsbackend.dto.technicaltestdto.SubmitTestRequest;
import com.main.icrsbackend.dto.SubmitTestResponse;
import com.main.icrsbackend.dto.technicaltestdto.TestSetResponse;
import com.main.icrsbackend.service.TechnicalTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class TechnicalTestController {

    private final TechnicalTestService technicalTestService;

    @GetMapping("/random")
    public TestSetResponse getRandomSet(
            @RequestParam(required = false) Long userId,
            @RequestParam String stream,
            @RequestParam String skill,
            @RequestParam String level
    ) {
        return technicalTestService.getRandomSet(userId, stream, skill, level);
    }

    @PostMapping("/submit")
    public SubmitTestResponse submitTest(@RequestBody SubmitTestRequest request) {
        return technicalTestService.submitTest(request);
    }

    @GetMapping("/level-status")
    public Map<String, Boolean> getLevelStatus(
            @RequestParam Long userId,
            @RequestParam String skill
    ) {
        return technicalTestService.getLevelStatus(userId, skill);
    }
}