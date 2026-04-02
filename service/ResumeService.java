package com.main.icrsbackend.service;

import com.main.icrsbackend.model.resume.Resume;
import com.main.icrsbackend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public Resume saveOrUpdate(Resume incoming) {
        Resume resume = resumeRepository.findByUserId(incoming.getUserId())
                .orElse(new Resume());

        if (resume.getId() == null) {
            resume.setUserId(incoming.getUserId());
        }

        resume.setSelectedTemplate(incoming.getSelectedTemplate());
        resume.setFullName(incoming.getFullName());
        resume.setRole(incoming.getRole());
        resume.setPhone(incoming.getPhone());
        resume.setLocation(incoming.getLocation());
        resume.setEmail(incoming.getEmail());
        resume.setLinkedin(incoming.getLinkedin());
        resume.setPortfolio(incoming.getPortfolio());
        resume.setProfilePhoto(incoming.getProfilePhoto());
        resume.setObjective(incoming.getObjective());
        resume.setSkillsJson(incoming.getSkillsJson());
        resume.setEducationsJson(incoming.getEducationsJson());
        resume.setExperiencesJson(incoming.getExperiencesJson());
        resume.setLanguagesJson(incoming.getLanguagesJson());
        resume.setCertificationsJson(incoming.getCertificationsJson());
        resume.setProjectsJson(incoming.getProjectsJson());

        return resumeRepository.save(resume);
    }

    public Resume getByUserId(Long userId) {
        return resumeRepository.findByUserId(userId).orElse(null);
    }
}