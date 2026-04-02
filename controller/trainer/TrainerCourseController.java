package com.main.icrsbackend.controller.trainer;

import com.main.icrsbackend.dto.TrainerCourseDTO;
import com.main.icrsbackend.service.trainer.TrainerCourseService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TrainerCourseController {

    private final TrainerCourseService service;

    // 🔥 GET
    @GetMapping
    public List<TrainerCourseDTO> get(@RequestParam Long trainerId) {
        return service.getCourses(trainerId);
    }

    // 🔥 POST
    @PostMapping
    public TrainerCourseDTO create(@RequestBody TrainerCourseDTO dto) {
        return service.create(dto);
    }

    // 🔥 PUT
    @PutMapping("/{id}")
    public TrainerCourseDTO update(@PathVariable Long id,
                                   @RequestBody TrainerCourseDTO dto) {
        return service.update(id, dto);
    }

    // 🔥 DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}