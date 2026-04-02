package com.main.icrsbackend.service;

import com.main.icrsbackend.dto.ProfileResponse;
import com.main.icrsbackend.dto.feed.PostDTO;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.feed.Post;
import com.main.icrsbackend.model.jobseeker.Academics;
import com.main.icrsbackend.model.jobseeker.JobSeekerProfile;
import com.main.icrsbackend.model.jobseeker.UserCertification;
import com.main.icrsbackend.model.profile.EmployerProfile;
import com.main.icrsbackend.model.profile.InstitutionProfile;
import com.main.icrsbackend.model.profile.MentorProfile;
import com.main.icrsbackend.model.student.StudentProfile;
import com.main.icrsbackend.model.trainer.Trainercourse;
import com.main.icrsbackend.model.trainer.Trainerprofile;
import com.main.icrsbackend.model.trainer.Trainercertifications;
import com.main.icrsbackend.repository.UserRepository;
import com.main.icrsbackend.repository.FollowRepository;
import com.main.icrsbackend.repository.academics.UserAcademicsRepository;
import com.main.icrsbackend.repository.academics.UserCertificationRepository;
import com.main.icrsbackend.repository.feed.PostRepository;
import com.main.icrsbackend.repository.jobseeker.JobSeekerProfileRepository;
import com.main.icrsbackend.repository.profilerepository.InstitutionProfileRepository;
import com.main.icrsbackend.repository.profilerepository.MentorProfileRepository;
import com.main.icrsbackend.repository.trainer.Courserepo;
import com.main.icrsbackend.repository.trainer.Profilerepo;
import com.main.icrsbackend.repository.trainer.Certificationsrepo;
import com.main.icrsbackend.repository.student.StudentProfileRepository;
import com.main.icrsbackend.repository.profilerepository.EmployerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicProfileService {

    private final UserRepository userRepo;
    private final JobSeekerProfileRepository jobRepo;
    private final InstitutionProfileRepository instRepo;
    private final Profilerepo trainerRepo;
    private final FollowRepository followRepo;
    private final MentorProfileRepository mentorRepo;
    private final StudentProfileRepository studentRepo;
    private final EmployerProfileRepository employerRepo;
    private final Courserepo courseRepo;
    private final Certificationsrepo certificationRepo;
    private final UserCertificationRepository userCertificationRepository;
    private final UserAcademicsRepository userAcademicsRepository;
    private final PostRepository postRepo;

    public ProfileResponse getProfile(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProfileResponse res = new ProfileResponse();

        res.setUserId(userId);
        res.setEmail(user.getEmail());
        res.setRole(user.getRole());

        String role = user.getRole() == null ? "" : user.getRole().toUpperCase();

        // POSTS (FOR ALL USERS)
        List<Post> posts = postRepo.findByUserIdOrderByCreatedAtDesc(userId);
        res.setPosts(
                posts.stream().map(p -> {
                    PostDTO dto = new PostDTO();
                    dto.setId(p.getId());
                    dto.setMediaUrl(p.getMediaUrl());
                    dto.setMediaType(p.getMediaType() != null ? p.getMediaType().name() : null);
                    dto.setCaption(p.getCaption());
                    return dto;
                }).toList()
        );

        switch (role) {

            // JOB SEEKER
            case "JOB_SEEKER" -> {
                JobSeekerProfile p = jobRepo.findByUserId(userId).orElse(null);
                if (p != null) {
                    res.setFullName(p.getFullName());
                    res.setHeadline(p.getHeadline());
                    res.setLocation(p.getLocation());
                    res.setBio(p.getBio());
                    res.setProfilePhoto(p.getProfilePhoto());

                    Academics a = userAcademicsRepository.findByUserId(userId).orElse(null);
                    if (a != null) {
                        res.setBio((res.getBio() == null ? "" : res.getBio()) +
                                "\n🎓 " + a.getDegree() +
                                " - " + a.getField() +
                                "\n🏫 " + a.getCollege());
                    }

                    List<UserCertification> ce =
                            userCertificationRepository.findByUserIdOrderByIdAsc(userId);
                    if (!ce.isEmpty()) {
                        UserCertification c = ce.get(0);
                        res.setBio((res.getBio() == null ? "" : res.getBio()) +
                                "\n📜 " + c.getTitle() +
                                (c.getIssuer() != null ? " (" + c.getIssuer() + ")" : ""));
                    }
                }
            }

            // STUDENT
            case "STUDENT" -> {
                StudentProfile p = studentRepo.findByUserId(userId).orElse(null);
                if (p != null) {
                    res.setFullName(p.getFullName());
                    res.setHeadline(p.getHeadline());
                    res.setLocation(p.getLocation());
                    res.setBio(p.getBio());
                    res.setProfilePhoto(p.getProfilePhoto());

                    Academics a = userAcademicsRepository.findByUserId(userId).orElse(null);
                    if (a != null) {
                        res.setBio((res.getBio() == null ? "" : res.getBio()) +
                                "\n🎓 " + a.getDegree() +
                                " - " + a.getField() +
                                "\n🏫 " + a.getCollege());
                        res.setUniversity(a.getUniversity());
                        res.setMarks(a.getMarks());
                    }

                    List<UserCertification> certifications =
                            userCertificationRepository.findByUserIdOrderByIdAsc(userId);
                    if (!certifications.isEmpty()) {
                        UserCertification ce = certifications.get(0);
                        res.setBio((res.getBio() == null ? "" : res.getBio()) +
                                "\n📜 " + ce.getTitle());
                        res.setYear(ce.getYear());
                    }
                }
            }

            // MENTOR
            case "MENTOR" -> {
                MentorProfile p = mentorRepo.findByUserId(userId).orElse(null);
                if (p != null) {
                    res.setFullName(p.getUser() != null ? p.getUser().getFname() : null);
                    res.setHeadline("Mentor - " + (p.getSpecialization() != null ? p.getSpecialization() : ""));
                    String bio = (p.getBio() != null ? p.getBio() : "") +
                            "\nExperience: " + (p.getExperience() != null ? p.getExperience() : "N/A");
                    res.setBio(bio);
                    res.setProfilePhoto(p.getProfileImage());
                }
            }

            // TRAINER
            case "TRAINER" -> {
                Trainerprofile p = trainerRepo.findByUserId(userId).orElse(null);
                if (p != null) {
                    res.setFullName(p.getOrganizationName());
                    res.setHeadline("Trainer");
                    res.setLocation(p.getLocation());
                    res.setBio(p.getBio());
                    res.setProfilePhoto(p.getProfilePhoto());
                }

                List<Trainercourse> courses = courseRepo.findByUserIdOrderByCreatedAtDesc(userId);
                if (!courses.isEmpty()) {
                    Trainercourse c = courses.get(0); // latest course
                    res.setCategory("Course: " + c.getCategory());
                    res.setDescription(c.getDescription());
                    res.setMode(c.getMode());
                    res.setDuration(c.getDuration());
                    res.setFee(c.getFee());
                    res.setBio((res.getBio() == null ? "" : res.getBio()) + "\nCourse: " + c.getTitle());

                    List<Trainercertifications> certs =
                            certificationRepo.findByTrainerIdOrderByCreatedAtDesc(userId);
                    if (!certs.isEmpty()) {
                        Trainercertifications cert = certs.get(0);
                        res.setBio(res.getBio() +
                                "\nCertification: " + cert.getTitle() +
                                (cert.getIssuer() != null ? " (" + cert.getIssuer() + ")" : ""));
                        if (cert.getYear() != null) {
                            res.setBio(res.getBio() + " - " + cert.getYear());
                        }
                        if (cert.getFileUrl() != null) {
                            res.setBio(res.getBio() + "\nCertificate File: " + cert.getFileUrl());
                        }
                    }
                }
            }

            // INSTITUTION
            case "INSTITUTION" -> {
                InstitutionProfile i = instRepo.findByUserId(userId).orElse(null);
                if (i != null) {
                    res.setFullName(i.getInstitutionName());
                    res.setHeadline(i.getDesignation());
                    res.setLocation(i.getLocation());
                    res.setBio(i.getAbout());
                    res.setProfilePhoto(i.getLogo());
                }
            }

            // EMPLOYER
            case "EMPLOYER" -> {
                EmployerProfile p = employerRepo.findByUserId(userId).orElse(null);
                if (p != null) {
                    res.setFullName(p.getCompanyName());
                    res.setHeadline(p.getDesignation());
                    res.setLocation(p.getHeadquarters());
                    res.setBio(p.getCompanyDescription());
                    res.setProfilePhoto(p.getLogo());
                }
            }

            // DEFAULT
            default -> res.setFullName(user.getFname() != null ? user.getFname() : user.getEmail());
        }

        // FOLLOW COUNTS
        res.setFollowers(followRepo.countByFollowingId(userId));
        res.setFollowing(followRepo.countByFollowerId(userId));

        return res;
    }
}
