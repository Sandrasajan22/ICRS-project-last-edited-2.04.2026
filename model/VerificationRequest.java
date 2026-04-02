package com.main.icrsbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "verification_requests",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id")
        }
)
public class VerificationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // ================= PRIMARY KEY =================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= USER RELATION =================
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_verification_user")
    )
    @JsonIgnoreProperties({"password"})
    private User user;

    // ================= DOCUMENT PATHS =================
    @Column(name = "id_proof_path", length = 500)
    private String idProofPath;

    @Column(name = "certificate_path", length = 500)
    private String certificatePath;

    @Column(name = "other_proof_path", length = 500)
    private String otherProofPath;

    // ================= VERIFICATION STATUS =================
    /**
     * Allowed values:
     * PENDING | APPROVED | REJECTED
     *
     * This MUST be set explicitly in controller/service.
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    // ================= ADMIN REMARKS =================
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================= TIMESTAMPS =================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================= AUTO TIMESTAMP =================
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // 🔒 Safety: normalize but DO NOT invent status
        if (this.status != null) {
            this.status = this.status.toUpperCase();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status != null) {
            this.status = this.status.toUpperCase();
        }
    }
}
