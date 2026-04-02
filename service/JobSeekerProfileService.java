package com.main.icrsbackend.service;

import com.main.icrsbackend.dto.JobSeekerProfileDTO;
import com.main.icrsbackend.dto.JobSeekerProfileUpdateDTO;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.jobseeker.JobSeekerProfile;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.jobseeker.JobSeekerProfileRepository;
import com.main.icrsbackend.repository.FollowRepository;
import com.main.icrsbackend.repository.feed.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.*;

@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final FollowRepository followRepo;

    public JobSeekerProfileService(JobSeekerProfileRepository profileRepo, UserRepository userRepo, PostRepository postRepo, FollowRepository followRepo) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.followRepo = followRepo;
    }

    public JobSeekerProfileDTO getProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<JobSeekerProfile> profileOpt = profileRepo.findByUserId(userId);

        if (profileOpt.isEmpty()) {
            return JobSeekerProfileDTO.builder()
                    .userId(user.getId())
                    .fname(getUserFirstName(user))
                    .lname(getUserLastName(user))
                    .email(getUserEmail(user))
                    .fullName(getUserFirstName(user) + " " + getUserLastName(user))
                    .role(user.getRole())
                    .skills(new ArrayList<>())
                    .isInitial(true)
                    .postCount(postRepo.countByUserId(user.getId()))
                    .followerCount(followRepo.countByFollowingId(user.getId()))
                    .followingCount(followRepo.countByFollowerId(user.getId()))
                    .build();
        }

        return toDto(profileOpt.get());
    }

    public JobSeekerProfileDTO updateProfile(Long userId, JobSeekerProfileUpdateDTO body) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobSeekerProfile profile = profileRepo.findByUserId(userId)
                .orElseGet(() -> JobSeekerProfile.builder().user(user).skills(new ArrayList<>()).build());

        String fullName = safeTrim(body.getFullName());
        if (!fullName.isEmpty()) {
            profile.setFullName(fullName);

            // optional split into fname/lname for UI
            String[] parts = fullName.split("\\s+");
            profile.setFname(parts.length > 0 ? parts[0] : "");
            profile.setLname(parts.length > 1 ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)) : "");
        }

        profile.setPhone(safeTrim(body.getPhone()));
        profile.setLocation(safeTrim(body.getLocation()));
        profile.setHeadline(safeTrim(body.getHeadline()));
        profile.setBio(safeTrim(body.getBio()));

        profile.setLinkedin(safeTrim(body.getLinkedin()));
        profile.setGithub(safeTrim(body.getGithub()));
        profile.setPortfolio(safeTrim(body.getPortfolio()));

        // ✅ normalize skills: trim, remove empty, distinct, limit 10
        List<String> normalizedSkills = normalizeSkills(body.getSkills());
        profile.setSkills(new ArrayList<>(normalizedSkills));

        JobSeekerProfile saved = profileRepo.save(profile);
        return toDto(saved);
    }

    public JobSeekerProfileDTO uploadImage(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new RuntimeException("File is required");

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobSeekerProfile profile = profileRepo.findByUserId(userId)
                .orElseGet(() -> JobSeekerProfile.builder().user(user).skills(new ArrayList<>()).build());

        String contentType = (file.getContentType() == null) ? "" : file.getContentType().toLowerCase();
        if (!(contentType.contains("jpeg") || contentType.contains("jpg") || contentType.contains("png") || contentType.contains("webp"))) {
            throw new RuntimeException("Only JPG/PNG/WEBP images are allowed");
        }

        try {
            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) ext = original.substring(dot).toLowerCase();

            if (ext.isEmpty()) {
                if (contentType.contains("png")) ext = ".png";
                else if (contentType.contains("webp")) ext = ".webp";
                else ext = ".jpg";
            }

            String filename = "js_" + userId + "_" + UUID.randomUUID() + ext;

            // uploads/jobseeker/
            String baseDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "jobseeker";
            Path dirPath = Paths.get(baseDir);
            Files.createDirectories(dirPath);

            Path target = dirPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            profile.setProfilePhoto("/uploads/jobseeker/" + filename);

            JobSeekerProfile saved = profileRepo.save(profile);
            return toDto(saved);

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }

    // ---------------- helpers ----------------

    private List<String> normalizeSkills(List<String> skills) {
        if (skills == null) return List.of();

        return skills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.length() > 60 ? s.substring(0, 60) : s)
                .distinct()
                .limit(10)
                .toList();
    }

    private JobSeekerProfileDTO toDto(JobSeekerProfile p) {
        User u = p.getUser();

        return JobSeekerProfileDTO.builder()
                .userId(u.getId())
                .fname(nullToEmpty(p.getFname()))
                .lname(nullToEmpty(p.getLname()))
                .fullName(nullToEmpty(p.getFullName()))
                .email(nullToEmpty(getUserEmail(u)))
                .phone(nullToEmpty(p.getPhone()))
                .location(nullToEmpty(p.getLocation()))
                .headline(nullToEmpty(p.getHeadline()))
                .bio(nullToEmpty(p.getBio()))
                .skills(p.getSkills() == null ? new ArrayList<>() : p.getSkills())
                .linkedin(nullToEmpty(p.getLinkedin()))
                .github(nullToEmpty(p.getGithub()))
                .portfolio(nullToEmpty(p.getPortfolio()))
                .profilePhoto(nullToEmpty(p.getProfilePhoto()))
                .role(u.getRole())
                .isInitial(false)
                .postCount(postRepo.countByUserId(u.getId()))
                .followerCount(followRepo.countByFollowingId(u.getId()))
                .followingCount(followRepo.countByFollowerId(u.getId()))
                .build();
    }

    private String safeTrim(String s) { return s == null ? "" : s.trim(); }
    private String nullToEmpty(String s) { return s == null ? "" : s; }

    private String buildFullName(String f, String l) {
        return (safeTrim(f) + " " + safeTrim(l)).trim();
    }

    // ✅ If your User has getEmail/getFname/getLname, replace these with direct calls.
    private String getUserEmail(User user) {
        try { return (String) user.getClass().getMethod("getEmail").invoke(user); }
        catch (Exception ignored) { return ""; }
    }

    private String getUserFirstName(User user) {
        for (String m : List.of("getFname", "getFirstName")) {
            try {
                Object v = user.getClass().getMethod(m).invoke(user);
                return v == null ? "" : v.toString();
            } catch (Exception ignored) {}
        }
        return "";
    }

    private String getUserLastName(User user) {
        for (String m : List.of("getLname", "getLastName")) {
            try {
                Object v = user.getClass().getMethod(m).invoke(user);
                return v == null ? "" : v.toString();
            } catch (Exception ignored) {}
        }
        return "";
    }
}