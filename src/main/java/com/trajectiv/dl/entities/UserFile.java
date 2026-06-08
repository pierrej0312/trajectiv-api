package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.FileStatus;
import com.trajectiv.dl.enums.UserFileKind;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "user_files",
        indexes = {
                @Index(name = "idx_user_files_owner_user_id", columnList = "owner_user_id"),
                @Index(name = "idx_user_files_kind", columnList = "kind"),
                @Index(name = "idx_user_files_status", columnList = "status"),
                @Index(name = "idx_user_files_deleted_at", columnList = "deleted_at")
        }
)
@ToString(exclude = {"ownerUser"})
public class UserFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false, updatable = false)
    private User ownerUser;

    @Column(name = "storage_key", nullable = false, unique = true, length = 500, updatable = false)
    private String storageKey;

    @Column(name = "public_url", length = 1000)
    private String publicUrl;

    @Column(name = "original_filename", length = 255, updatable = false)
    private String originalFilename;

    @Column(name = "mime_type", nullable = false, length = 120, updatable = false)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false, updatable = false)
    private long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 40, updatable = false)
    private UserFileKind kind;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private FileStatus status = FileStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected UserFile() {
    }

    private UserFile(
            User ownerUser,
            String storageKey,
            String publicUrl,
            String originalFilename,
            String mimeType,
            long sizeBytes,
            UserFileKind kind,
            FileStatus status
    ) {
        this.ownerUser = ownerUser;
        this.storageKey = storageKey;
        this.publicUrl = publicUrl;
        this.originalFilename = originalFilename;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.kind = kind;
        this.status = status;
    }

    public static UserFile createReady(
            User ownerUser,
            String storageKey,
            String publicUrl,
            String originalFilename,
            String mimeType,
            long sizeBytes,
            UserFileKind kind
    ) {
        return new UserFile(
                ownerUser,
                storageKey,
                publicUrl,
                originalFilename,
                mimeType,
                sizeBytes,
                kind,
                FileStatus.READY
        );
    }

    public void markReady(String publicUrl) {
        this.publicUrl = publicUrl;
        this.status = FileStatus.READY;
    }

    public void markFailed() {
        this.status = FileStatus.FAILED;
    }

    public void softDelete() {
        this.status = FileStatus.DELETED;
        this.deletedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = FileStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}