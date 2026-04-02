package com.main.icrsbackend.service;

import com.main.icrsbackend.dto.academics.AcademicsRequest;
import com.main.icrsbackend.dto.academics.AcademicsResponse;
import com.main.icrsbackend.dto.academics.CertificationRequest;
import com.main.icrsbackend.dto.academics.CertificationResponse;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.jobseeker.Academics;
import com.main.icrsbackend.model.jobseeker.UserCertification;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.academics.UserAcademicsRepository;
import com.main.icrsbackend.repository.academics.UserCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcademicsService {

    private final UserRepository userRepository;
    private final UserAcademicsRepository userAcademicsRepository;
    private final UserCertificationRepository userCertificationRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public AcademicsResponse getAcademics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateRole(user);

        Academics academics = userAcademicsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Academics a = new Academics();
                    a.setUserId(userId);
                    return userAcademicsRepository.save(a);
                });

        List<UserCertification> certs = userCertificationRepository.findByUserIdOrderByIdAsc(userId);

        return map(academics, certs);
    }

    public AcademicsResponse updateAcademics(Long userId, AcademicsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateRole(user);

        Academics academics = userAcademicsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Academics a = new Academics();
                    a.setUserId(userId);
                    return a;
                });

        academics.setUserId(userId);
        academics.setDegree(trim(request.getDegree()));
        academics.setField(trim(request.getField()));
        academics.setCollege(trim(request.getCollege()));
        academics.setUniversity(trim(request.getUniversity()));
        academics.setYear(trim(request.getYear()));
        academics.setMarks(trim(request.getMarks()));

        Academics savedAcademics = userAcademicsRepository.save(academics);

        userCertificationRepository.deleteByUserId(userId);

        List<UserCertification> savedCerts = new ArrayList<>();

        if (request.getCertifications() != null) {
            for (CertificationRequest item : request.getCertifications()) {
                boolean empty =
                        isBlank(item.getTitle()) &&
                                isBlank(item.getIssuer()) &&
                                isBlank(item.getYear()) &&
                                isBlank(item.getDescription()) &&
                                isBlank(item.getImageUrl());

                if (empty) continue;

                UserCertification cert = new UserCertification();
                cert.setUserId(userId);
                cert.setTitle(trim(item.getTitle()));
                cert.setIssuer(trim(item.getIssuer()));
                cert.setYear(trim(item.getYear()));
                cert.setDescription(trim(item.getDescription()));
                cert.setImagePath(trim(item.getImageUrl()));

                savedCerts.add(userCertificationRepository.save(cert));
            }
        }

        return map(savedAcademics, savedCerts);
    }

    public List<CertificationResponse> uploadCertificate(Long userId, MultipartFile file, Integer index) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateRole(user);

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        String contentType = file.getContentType() == null ? "" : file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        try {
            Path root = Paths.get(uploadDir, "academics-certificates");
            Files.createDirectories(root);

            String original = file.getOriginalFilename() == null ? "certificate.png" : file.getOriginalFilename();
            String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : ".png";
            String fileName = UUID.randomUUID() + ext;

            Path target = root.resolve(fileName);
            Files.copy(file.getInputStream(), target);

            String relativePath = "/uploads/academics-certificates/" + fileName;

            List<UserCertification> certs = userCertificationRepository.findByUserIdOrderByIdAsc(userId);

            if (index == null || index < 0 || index >= certs.size()) {
                throw new RuntimeException("Invalid certificate index");
            }

            UserCertification cert = certs.get(index);
            cert.setImagePath(relativePath);
            userCertificationRepository.save(cert);

            return userCertificationRepository.findByUserIdOrderByIdAsc(userId)
                    .stream()
                    .map(this::mapCert)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload certificate");
        }
    }

    private AcademicsResponse map(Academics academics, List<UserCertification> certs) {
        return AcademicsResponse.builder()
                .userId(academics.getUserId())
                .degree(academics.getDegree())
                .field(academics.getField())
                .college(academics.getCollege())
                .university(academics.getUniversity())
                .year(academics.getYear())
                .marks(academics.getMarks())
                .certifications(certs.stream().map(this::mapCert).toList())
                .build();
    }

    private CertificationResponse mapCert(UserCertification cert) {
        return CertificationResponse.builder()
                .id(cert.getId())
                .title(cert.getTitle())
                .issuer(cert.getIssuer())
                .year(cert.getYear())
                .description(cert.getDescription())
                .imageUrl(cert.getImagePath())
                .build();
    }

    private void validateRole(User user) {
        String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
        if (!role.equals("student") && !role.equals("job_seeker")) {
            throw new RuntimeException("Only student or job seeker can access academics");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}