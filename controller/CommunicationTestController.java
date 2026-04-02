package com.main.icrsbackend.controller;

import com.main.icrsbackend.dto.SubmitTestResponse;
import com.main.icrsbackend.dto.technicaltestdto.SubmitTestRequest;
import com.main.icrsbackend.dto.technicaltestdto.TestSetResponse;
import com.main.icrsbackend.service.CommunicationTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/communication-test")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CommunicationTestController {

    private final CommunicationTestService service;

    @GetMapping("/random")
    public TestSetResponse getTest(
            @RequestParam Long userId,
            @RequestParam String stream,
            @RequestParam String level
    ) {
        return service.getRandomSet(userId, stream, level);
    }

    @PostMapping("/submit")
    public SubmitTestResponse submit(@RequestBody SubmitTestRequest request) {
        return service.submitTest(request);
    }

    @GetMapping("/level-status")
    public Map<String, Boolean> getLevelStatus(
            @RequestParam Long userId,
            @RequestParam String stream
    ) {
        return service.getLevelStatus(userId, stream);
    }
}