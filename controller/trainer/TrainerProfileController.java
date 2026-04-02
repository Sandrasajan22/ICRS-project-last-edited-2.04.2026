package com.main.icrsbackend.controller.trainer;

import com.main.icrsbackend.dto.TrainerProfileDTO;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.trainer.Trainerprofile;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.trainer.Profilerepo;
import com.main.icrsbackend.repository.FollowRepository;
import com.main.icrsbackend.repository.feed.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/trainer/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class TrainerProfileController {

    @Autowired
    private Profilerepo profileRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private FollowRepository followRepo;

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir")
                    + File.separator + "uploads"
                    + File.separator + "trainer"
                    + File.separator;

    // ✅ GET: /api/trainer/profile/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        java.util.Optional<Trainerprofile> profileOpt = profileRepo.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.ok(TrainerProfileDTO.builder()
                    .userId(user.getId())
                    .fname(safe(user.getFname()))
                    .lname(safe(user.getLname()))
                    .role(user.getRole())
                    .isInitial(true)
                    .postCount(postRepo.countByUserId(userId))
                    .followerCount(followRepo.countByFollowingId(userId))
                    .followingCount(followRepo.countByFollowerId(userId))
                    .build());
        }

        return ResponseEntity.ok(toDto(profileOpt.get()));
    }

    // ✅ PUT: /api/trainer/profile/{userId} multipart/form-data
    @PutMapping(value = "/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestParam String fname,
            @RequestParam(required = false) String lname,
            @RequestParam String phone,
            @RequestParam(required = false) String agency,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String about,
            @RequestParam(required = false) MultipartFile photo
    ) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Trainerprofile profile = profileRepo.findByUserId(userId)
                .orElseGet(() -> {
                    Trainerprofile p = new Trainerprofile();
                    p.setUser(user);
                    return p;
                });

        // ✅ fname/lname belongs to USER
        user.setFname(fname);
        user.setLname(lname);
        userRepo.save(user);

        // ✅ other fields belong to Trainerprofile
        profile.setPhone(phone);
        profile.setOrganizationName(agency); // agency -> organizationName
        profile.setLocation(location);
        profile.setState(state);
        profile.setBio(about); // about -> bio

        // ✅ photo upload
        if (photo != null && !photo.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(UPLOAD_DIR));

                String ext = StringUtils.getFilenameExtension(photo.getOriginalFilename());
                if (ext == null || ext.isBlank()) ext = "jpg";

                String fileName = "trainer_" + userId + "_" + UUID.randomUUID() + "." + ext;
                Path filePath = Paths.get(UPLOAD_DIR + fileName);

                Files.write(filePath, photo.getBytes());

                // ✅ served by WebConfig: /uploads/**
                String publicUrl = "/uploads/trainer/" + fileName;

                profile.setProfilePhoto(publicUrl);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Photo upload failed: " + e.getMessage());
            }
        }

        Trainerprofile saved = profileRepo.save(profile);
        return ResponseEntity.ok(toDto(saved));
    }

    private TrainerProfileDTO toDto(Trainerprofile p) {
        User u = p.getUser();

        return TrainerProfileDTO.builder()
                .userId(u != null ? u.getId() : null)
                .fname(u != null ? safe(u.getFname()) : "")
                .lname(u != null ? safe(u.getLname()) : "")
                .phone(safe(p.getPhone()))
                .agency(safe(p.getOrganizationName()))
                .location(safe(p.getLocation()))
                .state(safe(p.getState()))
                .about(safe(p.getBio()))
                .profilePhotoUrl(safe(p.getProfilePhoto()))
                .role(u != null ? u.getRole() : "")
                .isInitial(false)
                .postCount(u != null ? postRepo.countByUserId(u.getId()) : 0L)
                .followerCount(u != null ? followRepo.countByFollowingId(u.getId()) : 0L)
                .followingCount(u != null ? followRepo.countByFollowerId(u.getId()) : 0L)
                .build();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}