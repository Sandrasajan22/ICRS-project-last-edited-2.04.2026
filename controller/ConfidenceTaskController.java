package com.main.icrsbackend.controller;

import com.main.icrsbackend.dto.confidencetaskdto.ConfidenceTaskSetResponse;
import com.main.icrsbackend.dto.confidencetaskdto.ConfidenceTaskSubmitRequest;
import com.main.icrsbackend.dto.confidencetaskdto.ConfidenceTaskSubmitResponse;
import com.main.icrsbackend.service.ConfidenceTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/confidence-tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ConfidenceTaskController {

    private final ConfidenceTaskService service;

    @GetMapping("/random")
    public ConfidenceTaskSetResponse getTasks(
            @RequestParam Long userId,
            @RequestParam String level
    ) {
        return service.getRandomSet(userId, level);
    }

    @PostMapping("/submit")
    public ConfidenceTaskSubmitResponse submitTasks(
            @RequestBody ConfidenceTaskSubmitRequest request
    ) {
        return service.submitTasks(request);
    }

    @GetMapping("/level-status")
    public Map<String, Boolean> getLevelStatus(@RequestParam Long userId) {
        return service.getLevelStatus(userId);
    }
}