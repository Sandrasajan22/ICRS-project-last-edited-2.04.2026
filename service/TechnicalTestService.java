package com.main.icrsbackend.service;

import com.main.icrsbackend.dto.*;
import com.main.icrsbackend.dto.technicaltestdto.AnswerRequest;
import com.main.icrsbackend.dto.technicaltestdto.QuestionResponse;
import com.main.icrsbackend.dto.technicaltestdto.SubmitTestRequest;
import com.main.icrsbackend.dto.technicaltestdto.TestSetResponse;
import com.main.icrsbackend.model.technicaltest.TechnicalQuestion;
import com.main.icrsbackend.model.technicaltest.TechnicalTestResult;
import com.main.icrsbackend.repository.technicaltestrepository.TechnicalQuestionRepository;
import com.main.icrsbackend.repository.technicaltestrepository.TechnicalTestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicalTestService {

    private final TechnicalQuestionRepository technicalQuestionRepository;
    private final TechnicalTestResultRepository technicalTestResultRepository;

    public TestSetResponse getRandomSet(Long userId, String stream, String skill, String level) {
        validateLevelAccess(userId, skill, level);

        int randomSet = new Random().nextInt(3) + 1;
        // change to 5 if your skill has 5 sets:
        // int randomSet = new Random().nextInt(5) + 1;

        List<TechnicalQuestion> questions =
                technicalQuestionRepository.findBySkillAndLevelAndSetNumber(
                        skill, level, randomSet
                );

        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found for selected skill, level, and set.");
        }

        List<QuestionResponse> questionResponses = questions.stream()
                .map(q -> QuestionResponse.builder()
                        .id(q.getId())
                        .question(q.getQuestion())
                        .options(List.of(
                                q.getOption1(),
                                q.getOption2(),
                                q.getOption3(),
                                q.getOption4()
                        ))
                        .difficulty(q.getDifficulty())
                        .questionType(q.getQuestionType())
                        .build())
                .collect(Collectors.toList());

        return TestSetResponse.builder()
                .stream(stream)
                .skill(skill)
                .level(level)
                .set_number(randomSet)
                .questions(questionResponses)
                .build();
    }

    public SubmitTestResponse submitTest(SubmitTestRequest request) {
        validateLevelAccess(request.getUserId(), request.getSkill(), request.getLevel());

        int score = 0;

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("No answers submitted.");
        }

        for (AnswerRequest answer : request.getAnswers()) {
            TechnicalQuestion question = technicalQuestionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + answer.getQuestionId()));

            if (question.getCorrectAnswer() != null &&
                    answer.getSelectedAnswer() != null &&
                    question.getCorrectAnswer().trim().equalsIgnoreCase(answer.getSelectedAnswer().trim())) {
                score++;
            }
        }

        String evaluation;
        if (score >= 8) {
            evaluation = "Excellent";
        } else if (score >= 5) {
            evaluation = "Good";
        } else {
            evaluation = "Needs Improvement";
        }

        TechnicalTestResult result = TechnicalTestResult.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .stream(request.getStream())
                .skill(request.getSkill())
                .level(request.getLevel())
                .setNumber(request.getSetNumber())
                .score(score)
                .totalQuestions(10)
                .finalEvaluation(evaluation)
                .submittedAt(LocalDateTime.now())
                .build();

        technicalTestResultRepository.save(result);

        return SubmitTestResponse.builder()
                .setScore(score)
                .levelScore((double) score)
                .finalEvaluation(evaluation)
                .build();
    }

    public Map<String, Boolean> getLevelStatus(Long userId, String skill) {
        Map<String, Boolean> map = new HashMap<>();

        map.put("Beginner", true);

        if (userId == null) {
            map.put("Intermediate", false);
            map.put("Difficult", false);
            return map;
        }

        boolean beginnerPassed =
                technicalTestResultRepository.existsByUserIdAndSkillAndLevelAndFinalEvaluationIn(
                        userId,
                        skill,
                        "Beginner",
                        List.of("Good", "Excellent")
                );

        boolean intermediatePassed =
                technicalTestResultRepository.existsByUserIdAndSkillAndLevelAndFinalEvaluationIn(
                        userId,
                        skill,
                        "Intermediate",
                        List.of("Good", "Excellent")
                );

        map.put("Intermediate", beginnerPassed);
        map.put("Difficult", intermediatePassed);

        return map;
    }

    private void validateLevelAccess(Long userId, String skill, String level) {
        if (level == null) return;

        if ("Beginner".equalsIgnoreCase(level)) {
            return;
        }

        if (userId == null) {
            throw new RuntimeException("Login required to unlock higher levels.");
        }

        if ("Intermediate".equalsIgnoreCase(level)) {
            boolean beginnerPassed =
                    technicalTestResultRepository.existsByUserIdAndSkillAndLevelAndFinalEvaluationIn(
                            userId,
                            skill,
                            "Beginner",
                            List.of("Good", "Excellent")
                    );

            if (!beginnerPassed) {
                throw new RuntimeException("Get at least 'Good' in Beginner to unlock Intermediate.");
            }
        }

        if ("Difficult".equalsIgnoreCase(level)) {
            boolean intermediatePassed =
                    technicalTestResultRepository.existsByUserIdAndSkillAndLevelAndFinalEvaluationIn(
                            userId,
                            skill,
                            "Intermediate",
                            List.of("Good", "Excellent")
                    );

            if (!intermediatePassed) {
                throw new RuntimeException("Get at least 'Good' in Intermediate to unlock Difficult.");
            }
        }
    }
}