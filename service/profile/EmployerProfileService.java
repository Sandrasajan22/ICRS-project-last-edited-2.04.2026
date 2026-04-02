package com.main.icrsbackend.service.profile;

import com.main.icrsbackend.dto.profiledto.EmployerProfileDTO;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.profile.EmployerProfile;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.profilerepository.EmployerProfileRepository;
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
public class EmployerProfileService {

    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepo;
    private final FollowRepository followRepo;

    private final Path uploadRoot = Paths.get(System.getProperty("user.dir"), "uploads", "employer-profiles").toAbsolutePath();

    @Transactional(readOnly = true)
    public EmployerProfileDTO getByUserId(Long userId) {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // validateRole(user, "employer");

        java.util.Optional<EmployerProfile> profileOpt = employerProfileRepository.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            EmployerProfileDTO dto = new EmployerProfileDTO();
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


    // UPDATE
    @Transactional
    public EmployerProfileDTO update(Long userId, EmployerProfileDTO dto, MultipartFile logo) throws IOException {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // validateRole(user, "employer");

        EmployerProfile profile = employerProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    EmployerProfile p = new EmployerProfile();
                    p.setUser(user);
                    return p;
                });

        profile.setCompanyName(dto.getCompanyName());
        profile.setCompanyWebsite(dto.getCompanyWebsite());
        profile.setIndustry(dto.getIndustry());
        profile.setCompanySize(dto.getCompanySize());
        profile.setHeadquarters(dto.getHeadquarters());
        profile.setHrName(dto.getHrName());
        profile.setDesignation(dto.getDesignation());
        profile.setCompanyDescription(dto.getCompanyDescription());
        profile.setHiringRoles(dto.getHiringRoles());
        profile.setLinkedinUrl(dto.getLinkedinUrl());
        profile.setAdditionalSkills(dto.getAdditionalSkills());

        // IMAGE
        if (logo != null && !logo.isEmpty()) {
            try {
                Files.createDirectories(uploadRoot);
                String filename = UUID.randomUUID() + "_" + Paths.get(logo.getOriginalFilename()).getFileName().toString();
                Path target = uploadRoot.resolve(filename);
                Files.copy(logo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                profile.setLogo("/uploads/employer-profiles/" + filename);
            } catch (IOException ioe) {
                log.error("Failed to store employer logo for user {}: {}", userId, ioe.getMessage(), ioe);
                throw ioe;
            }
        }

        EmployerProfile saved = employerProfileRepository.save(profile);
        return toDTO(user, saved);
    }

    private EmployerProfileDTO toDTO(User user, EmployerProfile profile) {
        EmployerProfileDTO dto = new EmployerProfileDTO();
        dto.setUserId(user.getId());
        dto.setFname(user.getFname());
        dto.setLname(user.getLname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setVerified(user.isVerified());
        dto.setVerificationStatus(user.getVerificationStatus() == null ? "" : user.getVerificationStatus());

        dto.setCompanyName(profile.getCompanyName());
        dto.setCompanyWebsite(profile.getCompanyWebsite());
        dto.setIndustry(profile.getIndustry());
        dto.setCompanySize(profile.getCompanySize());
        dto.setHeadquarters(profile.getHeadquarters());
        dto.setHrName(profile.getHrName());
        dto.setDesignation(profile.getDesignation());
        dto.setCompanyDescription(profile.getCompanyDescription());
        dto.setHiringRoles(profile.getHiringRoles());
        dto.setLinkedinUrl(profile.getLinkedinUrl());
        dto.setLogo(profile.getLogo());
        dto.setAdditionalSkills(profile.getAdditionalSkills());

        dto.setInitial(false);
        dto.setPostCount(postRepo.countByUserId(user.getId()));
        dto.setFollowerCount(followRepo.countByFollowingId(user.getId()));
        dto.setFollowingCount(followRepo.countByFollowerId(user.getId()));

        return dto;
    }
}
