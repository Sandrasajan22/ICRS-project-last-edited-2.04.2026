package com.main.icrsbackend.service.profile;

import com.main.icrsbackend.dto.profiledto.InstitutionProfileDTO;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.profile.InstitutionProfile;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.profilerepository.InstitutionProfileRepository;
import com.main.icrsbackend.repository.FollowRepository;
import com.main.icrsbackend.repository.feed.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionProfileService {

    private final InstitutionProfileRepository institutionProfileRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepo;
    private final FollowRepository followRepo;

    private final Path uploadRoot = Paths.get(System.getProperty("user.dir"), "uploads", "institution-profiles").toAbsolutePath();

    @Transactional(readOnly = true)
    public InstitutionProfileDTO getByUserId(Long userId) {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        validateRole(user, "institution");

        java.util.Optional<InstitutionProfile> profileOpt = institutionProfileRepository.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            InstitutionProfileDTO dto = new InstitutionProfileDTO();
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

    // UPDATE PROFILE
    @Transactional
    public InstitutionProfileDTO update(Long userId, InstitutionProfileDTO dto, MultipartFile logo) throws IOException {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        validateRole(user, "institution");

        InstitutionProfile profile = institutionProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    InstitutionProfile p = new InstitutionProfile();
                    p.setUser(user);
                    return p;
                });

        // update fields
        profile.setInstitutionName(dto.getInstitutionName());
        profile.setInstitutionType(dto.getInstitutionType());
        profile.setWebsite(dto.getWebsite());
        profile.setLocation(dto.getLocation());
        profile.setContactPerson(dto.getContactPerson());
        profile.setDesignation(dto.getDesignation());
        profile.setAbout(dto.getAbout());

        // If DTO uses List<String> for offeredPrograms, convert to storage format if needed.
        profile.setOfferedPrograms(dto.getOfferedPrograms());
        profile.setCertifications(dto.getCertifications());

        // IMAGE UPLOAD
        if (logo != null && !logo.isEmpty()) {
            try {
                Files.createDirectories(uploadRoot);
                String filename = UUID.randomUUID() + "_" + Paths.get(logo.getOriginalFilename()).getFileName().toString();
                Path target = uploadRoot.resolve(filename);
                Files.copy(logo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                profile.setLogo("/uploads/institution-profiles/" + filename);
            } catch (IOException ioe) {
                log.error("Failed to store institution logo for user {}: {}", userId, ioe.getMessage(), ioe);
                throw ioe;
            }
        }

        InstitutionProfile saved = institutionProfileRepository.save(profile);
        return toDTO(user, saved);
    }

    // DTO conversion
    private InstitutionProfileDTO toDTO(User user, InstitutionProfile profile) {
        InstitutionProfileDTO dto = new InstitutionProfileDTO();
        dto.setUserId(user.getId());
        dto.setFname(user.getFname());
        dto.setLname(user.getLname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setVerified(user.isVerified());
        dto.setVerificationStatus(user.getVerificationStatus() == null ? "" : user.getVerificationStatus());

        dto.setInstitutionName(profile.getInstitutionName());
        dto.setInstitutionType(profile.getInstitutionType());
        dto.setWebsite(profile.getWebsite());
        dto.setLocation(profile.getLocation());
        dto.setContactPerson(profile.getContactPerson());
        dto.setDesignation(profile.getDesignation());
        dto.setAbout(profile.getAbout());
        dto.setOfferedPrograms(profile.getOfferedPrograms());
        dto.setCertifications(profile.getCertifications());
        dto.setLogo(profile.getLogo());

        dto.setInitial(false);
        dto.setPostCount(postRepo.countByUserId(user.getId()));
        dto.setFollowerCount(followRepo.countByFollowingId(user.getId()));
        dto.setFollowingCount(followRepo.countByFollowerId(user.getId()));

        return dto;
    }

    // Role check
    private void validateRole(User user, String expectedRole) {
        if (user.getRole() == null || !user.getRole().equalsIgnoreCase(expectedRole)) {
            log.warn("User {} role mismatch: expected={}, actual={}", user.getId(), expectedRole, user.getRole());
            throw new AccessDeniedException("Access denied");
        }
    }
}
