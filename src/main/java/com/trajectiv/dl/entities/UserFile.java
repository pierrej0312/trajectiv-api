package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.FileStatus;
import com.trajectiv.dl.enums.UserFileKind;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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
@EqualsAndHashCode
@ToString(exclude = {"ownerUser"})
public class UserFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUser;

    @Setter
    @Column(name = "storage_key", nullable = false, unique = true, length = 500)
    private String storageKey;

    @Setter
    @Column(name = "public_url", length = 1000)
    private String publicUrl;

    @Setter
    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Setter
    @Column(name = "mime_type", nullable = false, length = 120)
    private String mimeType;

    @Setter
    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 40)
    private UserFileKind kind;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private FileStatus status = FileStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

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