package com.trajectiv.dl.entities;

import com.trajectiv.dl.enums.avatar.*;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "user_avatar_customizations",
        indexes = {
                @Index(name = "idx_user_avatar_customizations_user_id", columnList = "user_id"),
                @Index(name = "idx_user_avatar_customizations_body_type", columnList = "body_type"),
                @Index(name = "idx_user_avatar_customizations_hair_style", columnList = "hair_style")
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "sourcePhotoFile", "faceTextureFile"})
public class UserAvatarCustomization {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_type", nullable = false, length = 60)
    private AvatarBodyType bodyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_tone", nullable = false, length = 60)
    private AvatarSkinTone skinTone;

    @Column(name = "skin_intensity", nullable = false)
    private short skinIntensity;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_style", nullable = false, length = 60)
    private AvatarHairStyle hairStyle;

    @Column(name = "hair_color", nullable = false, length = 7)
    private String hairColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "beard_style", nullable = false, length = 60)
    private AvatarBeardStyle beardStyle;

    @Column(name = "beard_color", nullable = false, length = 7)
    private String beardColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_photo_file_id")
    private UserFile sourcePhotoFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "face_texture_file_id")
    private UserFile faceTextureFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "top_style", nullable = false, length = 60)
    private AvatarTopStyle topStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "bottom_style", nullable = false, length = 60)
    private AvatarBottomStyle bottomStyle;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserAvatarCustomization() {
    }

    private UserAvatarCustomization(
            User user,
            AvatarBodyType bodyType,
            AvatarSkinTone skinTone,
            short skinIntensity,
            AvatarHairStyle hairStyle,
            String hairColor,
            AvatarBeardStyle beardStyle,
            String beardColor,
            AvatarTopStyle topStyle,
            AvatarBottomStyle bottomStyle
    ) {
        this.user = user;
        this.bodyType = bodyType;
        this.skinTone = skinTone;
        this.skinIntensity = skinIntensity;
        this.hairStyle = hairStyle;
        this.hairColor = hairColor;
        this.beardStyle = beardStyle;
        this.beardColor = beardColor;
        this.topStyle = topStyle;
        this.bottomStyle = bottomStyle;
    }

    public static UserAvatarCustomization create(
            User user,
            AvatarBodyType bodyType,
            AvatarSkinTone skinTone,
            short skinIntensity,
            AvatarHairStyle hairStyle,
            String hairColor,
            AvatarBeardStyle beardStyle,
            String beardColor,
            AvatarTopStyle topStyle,
            AvatarBottomStyle bottomStyle
    ) {
        return new UserAvatarCustomization(
                user,
                bodyType,
                skinTone,
                skinIntensity,
                hairStyle,
                hairColor,
                beardStyle,
                beardColor,
                topStyle,
                bottomStyle
        );
    }

    public void patch(
            AvatarBodyType bodyType,
            AvatarSkinTone skinTone,
            Short skinIntensity,
            AvatarHairStyle hairStyle,
            String hairColor,
            AvatarBeardStyle beardStyle,
            String beardColor,
            AvatarTopStyle topStyle,
            AvatarBottomStyle bottomStyle
    ) {
        if (bodyType != null) {
            this.bodyType = bodyType;
        }

        if (skinTone != null) {
            this.skinTone = skinTone;
        }

        if (skinIntensity != null) {
            this.skinIntensity = skinIntensity;
        }

        if (hairStyle != null) {
            this.hairStyle = hairStyle;
        }

        if (hairColor != null && !hairColor.isBlank()) {
            this.hairColor = hairColor.trim();
        }

        if (beardStyle != null) {
            this.beardStyle = beardStyle;
        }

        if (beardColor != null && !beardColor.isBlank()) {
            this.beardColor = beardColor.trim();
        }

        if (topStyle != null) {
            this.topStyle = topStyle;
        }

        if (bottomStyle != null) {
            this.bottomStyle = bottomStyle;
        }
    }

    public void attachSourcePhoto(UserFile sourcePhotoFile) {
        this.sourcePhotoFile = sourcePhotoFile;
    }

    public void attachFaceTexture(UserFile faceTextureFile) {
        this.faceTextureFile = faceTextureFile;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
