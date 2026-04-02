package com.main.icrsbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.icrsbackend.dto.confidencetaskdto.*;
import com.main.icrsbackend.model.communicationandtasks.ConfidenceTask;
import com.main.icrsbackend.model.communicationandtasks.ConfidenceTaskResult;
import com.main.icrsbackend.repository.communicationandtasksrepository.ConfidenceTaskRepository;
import com.main.icrsbackend.repository.communicationandtasksrepository.ConfidenceTaskResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ConfidenceTaskService {

    private final ConfidenceTaskRepository taskRepository;
    private final ConfidenceTaskResultRepository resultRepository;
    private final ObjectMapper objectMapper;

    public ConfidenceTaskSetResponse getRandomSet(Long userId, String level) {
        validateLevelAccess(userId, level);

        int randomSet = new Random().nextInt(5) + 1;

        List<ConfidenceTask> tasks =
                taskRepository.findLimitedTasks(level, randomSet, PageRequest.of(0, 5));

        if (tasks == null || tasks.isEmpty()) {
            throw new RuntimeException("No confidence tasks found for selected level.");
        }

        List<ConfidenceTaskDTO> taskDTOs = tasks.stream()
                .map(t -> ConfidenceTaskDTO.builder()
                        .id(t.getId())
                        .task(t.getTask())
                        .instruction(t.getInstruction())
                        .expected_time(t.getExpectedTime())
                        .difficulty(t.getDifficulty())
                        .skill_focus(t.getSkillFocus())
                        .build())
                .toList();

        return ConfidenceTaskSetResponse.builder()
                .type("confidence_tasks")
                .level(level)
                .set_number(randomSet)
                .tasks(taskDTOs)
                .build();
    }

    public ConfidenceTaskSubmitResponse submitTasks(ConfidenceTaskSubmitRequest request) {
        if (request == null) {
            throw new RuntimeException("Request body is missing.");
        }

        if (request.getUserId() == null) {
            throw new RuntimeException("User ID is missing.");
        }

        if (request.getLevel() == null || request.getLevel().trim().isEmpty()) {
            throw new RuntimeException("Level is missing.");
        }

        if (request.getTasks() == null || request.getTasks().isEmpty()) {
            throw new RuntimeException("No task data submitted.");
        }

        int totalTasks = request.getTasks().size();

        long completedCount = request.getTasks().stream()
                .filter(t -> Boolean.TRUE.equals(t.getCompleted()))
                .count();

        int completionScore = (int) Math.round((completedCount * 100.0) / totalTasks);

        double avgRating = request.getTasks().stream()
                .filter(t -> t.getSelfRating() != null)
                .mapToInt(ConfidenceTaskSubmissionItemDTO::getSelfRating)
                .average()
                .orElse(0.0);

        int confidenceScore = (int) Math.round((avgRating / 5.0) * 100);

        long onTimeCount = request.getTasks().stream()
                .filter(t ->
                        Boolean.TRUE.equals(t.getCompleted()) &&
                                t.getTimeTaken() != null &&
                                t.getExpectedTime() != null &&
                                t.getTimeTaken() <= t.getExpectedTime()
                )
                .count();

        int consistencyScore = (int) Math.round((onTimeCount * 100.0) / totalTasks);

        String finalEvaluation;
        if (completionScore >= 80 && confidenceScore >= 70 && consistencyScore >= 60) {
            finalEvaluation = "Excellent";
        } else if (completionScore >= 60) {
            finalEvaluation = "Good";
        } else {
            finalEvaluation = "Needs Improvement";
        }

        try {
            ConfidenceTaskResult result = ConfidenceTaskResult.builder()
                    .userId(request.getUserId())
                    .name(request.getName())
                    .level(request.getLevel())
                    .setNumber(request.getSetNumber())
                    .completionScore(completionScore)
                    .confidenceScore(confidenceScore)
                    .consistencyScore(consistencyScore)
                    .finalEvaluation(finalEvaluation)
                    .submissionJson(objectMapper.writeValueAsString(request.getTasks()))
                    .submittedAt(LocalDateTime.now())
                    .build();

            resultRepository.save(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save confidence task result: " + e.getMessage());
        }

        return ConfidenceTaskSubmitResponse.builder()
                .completionScore(completionScore)
                .confidenceScore(confidenceScore)
                .consistencyScore(consistencyScore)
                .finalEvaluation(finalEvaluation)
                .build();
    }

    public Map<String, Boolean> getLevelStatus(Long userId) {
        Map<String, Boolean> map = new HashMap<>();
        map.put("Beginner", true);

        if (userId == null) {
            map.put("Intermediate", false);
            map.put("Difficult", false);
            return map;
        }

        boolean beginnerPassed =
                resultRepository.existsByUserIdAndLevelAndCompletionScoreGreaterThanEqual(
                        userId, "Beginner", 60
                );

        boolean intermediatePassed =
                resultRepository.existsByUserIdAndLevelAndCompletionScoreGreaterThanEqual(
                        userId, "Intermediate", 60
                );

        map.put("Intermediate", beginnerPassed);
        map.put("Difficult", intermediatePassed);

        return map;
    }

    private void validateLevelAccess(Long userId, String level) {
        if (level == null) return;

        if ("Beginner".equalsIgnoreCase(level)) {
            return;
        }

        if (userId == null) {
            throw new RuntimeException("Login required to unlock higher levels.");
        }

        if ("Intermediate".equalsIgnoreCase(level)) {
            boolean beginnerPassed =
                    resultRepository.existsByUserIdAndLevelAndCompletionScoreGreaterThanEqual(
                            userId, "Beginner", 60
                    );

            if (!beginnerPassed) {
                throw new RuntimeException("Complete Beginner with at least 60% to unlock Intermediate.");
            }
        }

        if ("Difficult".equalsIgnoreCase(level)) {
            boolean intermediatePassed =
                    resultRepository.existsByUserIdAndLevelAndCompletionScoreGreaterThanEqual(
                            userId, "Intermediate", 60
                    );

            if (!intermediatePassed) {
                throw new RuntimeException("Complete Intermediate with at least 60% to unlock Difficult.");
            }
        }
    }
}