package com.main.icrsbackend.service.trainer;



import com.main.icrsbackend.dto.TrainerCourseDTO;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.trainer.Trainercourse;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.trainer.Courserepo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerCourseService {

    private final Courserepo repo;
    private final UserRepository userRepo;

    // 🔥 GET ALL
    public List<TrainerCourseDTO> getCourses(Long trainerId) {
        return repo.findByUserIdOrderByCreatedAtDesc(trainerId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // 🔥 CREATE
    public TrainerCourseDTO create(TrainerCourseDTO dto) {

        User user = userRepo.findById(dto.getTrainerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trainercourse c = Trainercourse.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .mode(dto.getMode())
                .duration(dto.getDuration())
                .fee(dto.getFee())
                .location(dto.getLocation())
                .active(true)
                .build();

        return mapToDTO(repo.save(c));
    }

    // 🔥 UPDATE
    public TrainerCourseDTO update(Long id, TrainerCourseDTO dto) {

        Trainercourse c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        c.setTitle(dto.getTitle());
        c.setDescription(dto.getDescription());
        c.setCategory(dto.getCategory());
        c.setMode(dto.getMode());
        c.setDuration(dto.getDuration());
        c.setFee(dto.getFee());
        c.setLocation(dto.getLocation());

        return mapToDTO(repo.save(c));
    }

    // 🔥 DELETE
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // 🔥 MAPPER
    private TrainerCourseDTO mapToDTO(Trainercourse c) {
        return TrainerCourseDTO.builder()
                .id(c.getId())
                .trainerId(c.getUser().getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .mode(c.getMode())
                .duration(c.getDuration())
                .fee(c.getFee())
                .location(c.getLocation())
                .active(c.isActive())
                .build();
    }
}