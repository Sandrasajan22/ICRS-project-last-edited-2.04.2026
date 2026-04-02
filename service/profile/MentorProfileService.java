package com.main.icrsbackend.service.profile;

import com.main.icrsbackend.dto.profiledto.MentorProfileDTO;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.profile.MentorProfile;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.FollowRepository;
import com.main.icrsbackend.repository.feed.PostRepository;
import com.main.icrsbackend.repository.profilerepository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorProfileService {

    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepo;
    private final FollowRepository followRepo;

    private final Path uploadRoot = Paths.get(System.getProperty("user.dir"), "uploads", "mentor-profiles").toAbsolutePath();

    /**
     * Get mentor profile for a user and return DTO.
     */
    @Transactional(readOnly = true)
    public MentorProfileDTO getByUserId(Long userId) {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        validateRole(user, "mentor");

        Optional<MentorProfile> profileOpt = mentorProfileRepository.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            MentorProfileDTO dto = new MentorProfileDTO();
            dto.setUserId(user.getId());
            dto.setFname(user.getFname());
            dto.setLname(user.getLname());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            dto.setInitial(true);
            dto.setPostCount(postRepo.countByUserId(userId));
            dto.setFollowerCount(followRepo.countByFollowingId(userId));
            dto.setFollowingCount(followRepo.countByFollowerId(userId));
            return dto;
        }

        return toDTO(user, profileOpt.get());
    }

    /**
     * Update mentor profile. Saves uploaded image (if provided) and persists profile.
     */
    @Transactional
    public MentorProfileDTO update(Long userId, MentorProfileDTO dto, MultipartFile image) {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        validateRole(user, "mentor");

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    MentorProfile p = new MentorProfile();
                    p.setUser(user);
                    return p;
                });

        // Update scalar fields
        profile.setHeadline(dto.getHeadline());
        profile.setSpecialization(dto.getSpecialization());
        profile.setBio(dto.getBio());
        profile.setSkills(dto.getSkills()); // keep format consistent with entity
        profile.setExperience(dto.getExperience());
        profile.setCurrentOrganization(dto.getCurrentOrganization());
        profile.setDesignation(dto.getDesignation());
        profile.setLinkedinUrl(dto.getLinkedinUrl());

        // Handle image upload if present
        if (image != null && !image.isEmpty()) {
            try {
                Files.createDirectories(uploadRoot);
            } catch (IOException e) {
                log.error("Unable to create upload directory: {}", uploadRoot, e);
                throw new IllegalStateException("Failed to prepare upload directory", e);
            }

            String original = image.getOriginalFilename();
            String safeOriginal = (original == null) ? "image" : Paths.get(original).getFileName().toString();
            String filename = UUID.randomUUID().toString() + "_" + safeOriginal;
            Path target = uploadRoot.resolve(filename);

            try {
                Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                // store web-accessible path (adjust if your static resource mapping differs)
                profile.setProfileImage("/uploads/mentor-profiles/" + filename);
            } catch (IOException e) {
                log.error("Failed to store uploaded image for user {}: {}", userId, e.getMessage(), e);
                throw new IllegalStateException("Failed to store uploaded image", e);
            }
        }

        MentorProfile saved = mentorProfileRepository.save(profile);

        return toDTO(user, saved);
    }

    /**
     * Convert entity -> DTO (populate user + profile fields).
     */
    private MentorProfileDTO toDTO(User user, MentorProfile profile) {
        MentorProfileDTO dto = new MentorProfileDTO();

        // user fields
        dto.setUserId(user.getId());
        dto.setFname(user.getFname());
        dto.setLname(user.getLname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setVerified(user.isVerified());
        dto.setVerificationStatus(user.getVerificationStatus() == null ? "" : user.getVerificationStatus());

        // profile fields
        dto.setHeadline(profile.getHeadline());
        dto.setSpecialization(profile.getSpecialization());
        dto.setBio(profile.getBio());
        dto.setSkills(profile.getSkills());
        dto.setExperience(profile.getExperience());
        dto.setCurrentOrganization(profile.getCurrentOrganization());
        dto.setDesignation(profile.getDesignation());
        dto.setLinkedinUrl(profile.getLinkedinUrl());
        dto.setProfileImage(profile.getProfileImage());
        dto.setInitial(false);
        dto.setPostCount(postRepo.countByUserId(user.getId()));
        dto.setFollowerCount(followRepo.countByFollowingId(user.getId()));
        dto.setFollowingCount(followRepo.countByFollowerId(user.getId()));

        return dto;
    }

    /**
     * Validate that the user has the expected role.
     */
    private void validateRole(User user, String expectedRole) {
        if (user.getRole() == null || !user.getRole().equalsIgnoreCase(expectedRole)) {
            log.warn("Access denied for user {}: expected role={}, actual={}", user.getId(), expectedRole, user.getRole());
            throw new AccessDeniedException("Access denied: user does not have role " + expectedRole);
        }
    }
}
