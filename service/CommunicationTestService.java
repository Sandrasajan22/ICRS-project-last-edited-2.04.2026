package com.main.icrsbackend.service;

import com.main.icrsbackend.dto.SubmitTestResponse;
import com.main.icrsbackend.dto.technicaltestdto.AnswerRequest;
import com.main.icrsbackend.dto.technicaltestdto.QuestionResponse;
import com.main.icrsbackend.dto.technicaltestdto.SubmitTestRequest;
import com.main.icrsbackend.dto.technicaltestdto.TestSetResponse;
import com.main.icrsbackend.model.communicationandtasks.CommunicationQuestion;
import com.main.icrsbackend.model.communicationandtasks.CommunicationTestResult;
import com.main.icrsbackend.repository.communicationandtasksrepository.CommunicationQuestionRepository;
import com.main.icrsbackend.repository.communicationandtasksrepository.CommunicationTestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CommunicationTestService {

    private final CommunicationQuestionRepository questionRepository;
    private final CommunicationTestResultRepository resultRepository;

    public TestSetResponse getRandomSet(Long userId, String stream, String level) {
        validateLevelAccess(userId, stream, level);

        int randomSet = new Random().nextInt(5) + 1;

        List<CommunicationQuestion> questions =
                questionRepository.findLimitedQuestions(
                        stream,
                        level,
                        randomSet,
                        PageRequest.of(0, 10)
                );

        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("No questions found for selected stream, level, and set.");
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
                .toList();

        return TestSetResponse.builder()
                .stream(stream)
                .skill("communication")
                .level(level)
                .set_number(randomSet)
                .questions(questionResponses)
                .build();
    }

    public SubmitTestResponse submitTest(SubmitTestRequest request) {
        validateLevelAccess(request.getUserId(), request.getStream(), request.getLevel());

        int score = 0;

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("No answers submitted.");
        }

        for (AnswerRequest answer : request.getAnswers()) {
            CommunicationQuestion question = questionRepository.findById(answer.getQuestionId())
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
            evaluation = "Good communication";
        } else {
            evaluation = "Basic understanding";
        }

        CommunicationTestResult result = CommunicationTestResult.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .stream(request.getStream())
                .level(request.getLevel())
                .setNumber(request.getSetNumber())
                .score(score)
                .totalQuestions(10)
                .finalEvaluation(evaluation)
                .submittedAt(LocalDateTime.now())
                .build();

        resultRepository.save(result);

        return SubmitTestResponse.builder()
                .setScore(score)
                .levelScore((double) score)
                .finalEvaluation(evaluation)
                .build();
    }

    public Map<String, Boolean> getLevelStatus(Long userId, String stream) {
        Map<String, Boolean> map = new HashMap<>();

        map.put("Beginner", true);

        if (userId == null) {
            map.put("Intermediate", false);
            map.put("Difficult", false);
            return map;
        }

        boolean beginnerPassed =
                resultRepository.existsByUserIdAndStreamAndLevelAndFinalEvaluationIn(
                        userId,
                        stream,
                        "Beginner",
                        List.of("Good communication", "Excellent")
                );

        boolean intermediatePassed =
                resultRepository.existsByUserIdAndStreamAndLevelAndFinalEvaluationIn(
                        userId,
                        stream,
                        "Intermediate",
                        List.of("Good communication", "Excellent")
                );

        map.put("Intermediate", beginnerPassed);
        map.put("Difficult", intermediatePassed);

        return map;
    }

    private void validateLevelAccess(Long userId, String stream, String level) {
        if (level == null) return;

        if ("Beginner".equalsIgnoreCase(level)) {
            return;
        }

        if (userId == null) {
            throw new RuntimeException("Login required to unlock higher levels.");
        }

        if ("Intermediate".equalsIgnoreCase(level)) {
            boolean beginnerPassed =
                    resultRepository.existsByUserIdAndStreamAndLevelAndFinalEvaluationIn(
                            userId,
                            stream,
                            "Beginner",
                            List.of("Good communication", "Excellent")
                    );

            if (!beginnerPassed) {
                throw new RuntimeException("Get at least 'Good communication' in Beginner to unlock Intermediate.");
            }
        }

        if ("Difficult".equalsIgnoreCase(level)) {
            boolean intermediatePassed =
                    resultRepository.existsByUserIdAndStreamAndLevelAndFinalEvaluationIn(
                            userId,
                            stream,
                            "Intermediate",
                            List.of("Good communication", "Excellent")
                    );

            if (!intermediatePassed) {
                throw new RuntimeException("Get at least 'Good communication' in Intermediate to unlock Difficult.");
            }
        }
    }
}