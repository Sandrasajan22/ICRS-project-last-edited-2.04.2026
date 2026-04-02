package com.main.icrsbackend.service.feed;

import com.main.icrsbackend.dto.feed.FeedPostResponse;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.interview.FeedbackRepository;
import com.main.icrsbackend.repository.academics.UserCertificationRepository;
import com.main.icrsbackend.repository.FollowRepository;
import com.main.icrsbackend.repository.academics.UserAcademicsRepository;
import com.main.icrsbackend.model.interview.InterviewFeedback;
import com.main.icrsbackend.model.jobseeker.Academics;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepo;
    private final UserCertificationRepository certRepo;
    private final FollowRepository followRepo;
    private final UserAcademicsRepository academicsRepo;
    private final RestTemplate restTemplate;

    private final String FLASK_AI_URL = "http://127.0.0.1:5000";

    /**
     * Sends posts to the ML model to rank them dynamically.
     */
    public List<FeedPostResponse> getRankedFeed(Long userId, List<FeedPostResponse> posts) {
        if (posts == null || posts.isEmpty()) return posts;

        User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
        Map<String, Object> userData = buildFullUserMlData(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("user_data", userData);
        payload.put("user_type", user != null ? user.getRole().toLowerCase() : "guest");

        List<Map<String, Object>> mlPosts = posts.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("author_id", p.getUserId());
            map.put("likes", p.getLikeCount());
            map.put("tags", parseTags(p.getTags()));
            return map;
        }).collect(Collectors.toList());

        payload.put("posts", mlPosts);

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    FLASK_AI_URL + "/feed/rank",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (response.getBody() != null) {
                List<Map<String, Object>> rankedList = response.getBody();
                List<Long> sortedIds = rankedList.stream()
                        .map(m -> Long.valueOf(m.get("id").toString()))
                        .toList();

                List<FeedPostResponse> sorted = new ArrayList<>(posts);
                sorted.sort((a, b) -> {
                    int idxA = sortedIds.indexOf(a.getId());
                    int idxB = sortedIds.indexOf(b.getId());
                    return Integer.compare(idxA, idxB);
                });
                return sorted;
            }
        } catch (Exception e) {
            System.err.println("[AI Integration] Feed ranking failed: " + e.getMessage());
        }
        return posts;
    }

    /**
     * Proxies the full performance dashboard request to the AI engine.
     */
    public Map<String, Object> getFullDashboard(Long userId, Map<String, Object> extraDetails) {
        User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
        Map<String, Object> fullUserData = buildFullUserMlData(user);

        if (extraDetails != null) fullUserData.putAll(extraDetails);

        Map<String, Object> payload = new HashMap<>();
        payload.put("user_data", fullUserData);

        try {
            return restTemplate.postForObject(FLASK_AI_URL + "/dashboard", payload, Map.class);
        } catch (Exception e) {
            return Map.of("error", "AI Engine offline", "status", "offline");
        }
    }

    private Map<String, Object> buildFullUserMlData(User user) {
        Map<String, Object> data = new HashMap<>();
        if (user == null) return data;

        Long uid = user.getId();
        data.put("user_id", uid);
        data.put("role", user.getRole().toLowerCase());

        List<InterviewFeedback> reviews = feedbackRepo.findByStudent_Id(uid);
        double tech = reviews.stream().mapToDouble(InterviewFeedback::getTechnicalScore).average().orElse(0.0);
        double comm = reviews.stream().mapToDouble(InterviewFeedback::getCommunicationScore).average().orElse(0.0);
        double conf = reviews.stream().mapToDouble(InterviewFeedback::getConfidenceScore).average().orElse(0.0);

        data.put("technical_score", tech);
        data.put("communication_score", comm);
        data.put("confidence_score", conf);
        data.put("mock_interview_score", reviews.isEmpty() ? 5.0 : (tech + comm + conf) / 3.0);

        data.put("certifications_count", certRepo.countByUserId(uid));
        data.put("follows_count", followRepo.countByFollowingId(uid));

        Academics ac = academicsRepo.findByUserId(uid).orElse(null);
        data.put("aptitude_score", ac != null ? 7.5 : 5.0);
        data.put("projects_count", 2);
        data.put("internships_count", 0);
        data.put("coding_platform_score", 6);
        data.put("resume_score", 7);
        data.put("linkedin_score", 6);

        return data;
    }

    private List<String> parseTags(String str) {
        if (str == null || str.isBlank()) return List.of();
        return List.of(str.split("\\s*,\\s*"));
    }
}