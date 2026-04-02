package com.main.icrsbackend.service;

import com.main.icrsbackend.model.Complaint;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.repository.ComplaintRepository;
import com.main.icrsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotificationService notificationService;

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "complaints" + File.separator;

    private static final String PUBLIC_URL_PREFIX = "/uploads/complaints/";

    public Complaint submitComplaint(Long userId, String subject, String description, MultipartFile screenshot) throws IOException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Complaint complaint = new Complaint();
        complaint.setReporter(user);
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setStatus("PENDING");
        complaint.setCreatedAt(LocalDateTime.now());

        if (screenshot != null && !screenshot.isEmpty()) {
            createUploadFolder();
            String fileName = "COMPLAINT_" + UUID.randomUUID() + "_" + screenshot.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            File destination = new File(UPLOAD_DIR + fileName);
            screenshot.transferTo(destination);
            complaint.setScreenshotPath(PUBLIC_URL_PREFIX + fileName);
        }

        Complaint saved = complaintRepo.save(complaint);

        // Notify all admins (recipientId = 0L means admin broadcast)
        notificationService.create(
                0L,
                "New complaint from " + user.getFname() + " " + user.getLname() + ": " + subject,
                "COMPLAINT",
                "/admin/complaints",
                saved.getId()
        );

        return saved;
    }

    public List<Complaint> getAllPending() {
        return complaintRepo.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    public List<Complaint> getResolvedForUser(Long userId) {
        return complaintRepo.findByReporter_Id(userId);
    }

    public Complaint resolveComplaint(Long id, String resolutionNote) {
        Complaint complaint = complaintRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus("RESOLVED");
        complaint.setResolutionNote(resolutionNote);
        complaint.setResolvedAt(LocalDateTime.now());

        Complaint saved = complaintRepo.save(complaint);

        // Notify the reporter that their complaint was resolved
        notificationService.create(
                saved.getReporter().getId(),
                "Your complaint '" + saved.getSubject() + "' has been resolved. Tap to view the resolution.",
                "COMPLAINT_RESOLVED",
                "/complaints",
                saved.getId()
        );

        return saved;
    }

    private void createUploadFolder() {
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) folder.mkdirs();
    }
}
