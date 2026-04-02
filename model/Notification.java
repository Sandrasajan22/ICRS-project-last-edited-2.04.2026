package com.main.icrsbackend.model;


import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ex: "New verification request from Sandra"
    @Setter
    @Column(nullable = false, length = 255)
    private String message;

    // ex: VERIFICATION, COMPLAINT, USER
    @Setter
    @Column(nullable = false, length = 30)
    private String type;

    // ex: /admin/verification or /admin/complaints
    @Setter
    @Column(nullable = false, length = 100)
    private String redirectPath;

    // optional: link id like verificationRequestId or complaintId
    @Setter
    private Long refId;

    @Setter
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId = 0L; // 0L for admin/broadcast, otherwise specific userId

    @Setter
    @Column(nullable = false)
    private boolean readStatus = false;

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {}

    public Notification(Long recipientId, String message, String type, String redirectPath, Long refId) {
        this.recipientId = recipientId;
        this.message = message;
        this.type = type;
        this.redirectPath = redirectPath;
        this.refId = refId;
        this.readStatus = false;
        this.createdAt = LocalDateTime.now();
    }

    // getters/setters (generate from IDE)
    public Long getId() { return id; }
    public Long getRecipientId() { return recipientId; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getRedirectPath() { return redirectPath; }
    public Long getRefId() { return refId; }
    public boolean isReadStatus() { return readStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
