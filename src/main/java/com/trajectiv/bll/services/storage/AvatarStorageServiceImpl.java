package com.trajectiv.bll.services.storage;

import com.trajectiv.bll.dto.storage.StorageCommandBllDto;
import com.trajectiv.bll.dto.storage.StoredAvatarBllDto;
import com.trajectiv.bll.dto.storage.StoredFileBllDto;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.services.me.sync.UserSyncService;
import com.trajectiv.config.storage.StorageProperties;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserFile;
import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.enums.file.UserFileKind;
import com.trajectiv.dl.repositories.UserFileRepository;
import com.trajectiv.dl.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarStorageServiceImpl implements AvatarStorageService {

    private static final String MIME_JPEG = "image/jpeg";
    private static final String MIME_PNG = "image/png";
    private static final String MIME_WEBP = "image/webp";

    private final UserSyncService userSyncService;
    private final UserProfileRepository userProfileRepository;
    private final UserFileRepository userFileRepository;
    private final FileStorageService fileStorageService;
    private final StorageProperties storageProperties;

    @Override
    @Transactional
    public StoredAvatarBllDto uploadCurrentUserAvatar(Authentication authentication, MultipartFile file) {
        User user = userSyncService.syncFromAuthentication(authentication);

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserContextInitializationException(
                        BusinessErrorCode.USER_PROFILE_NOT_INITIALIZED,
                        user.getId(),
                        "user_profile"
                ));

        validateAvatar(file);

        deleteExistingAvatarIfAny(profile);

        String extension = resolveExtension(file.getContentType());
        String directory = "users/" + user.getId() + "/avatar";
        String targetFilename = "avatar-" + UUID.randomUUID() + "." + extension;

        try {
            StoredFileBllDto storedFile = fileStorageService.store(new StorageCommandBllDto(
                    user.getId(),
                    directory,
                    targetFilename,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getInputStream()
            ));

            UserFile userFile = UserFile.createReady(
                    user,
                    storedFile.storageKey(),
                    storedFile.publicUrl(),
                    storedFile.originalFilename(),
                    storedFile.mimeType(),
                    storedFile.sizeBytes(),
                    UserFileKind.AVATAR
            );

            UserFile savedUserFile = userFileRepository.save(userFile);

            profile.updateAvatar(savedUserFile, storedFile.publicUrl());
            userProfileRepository.save(profile);

            return new StoredAvatarBllDto(
                    savedUserFile.getId(),
                    storedFile.publicUrl()
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read uploaded avatar file.", exception);
        }
    }

    @Override
    @Transactional
    public StoredAvatarBllDto deleteCurrentUserAvatar(Authentication authentication) {
        User user = userSyncService.syncFromAuthentication(authentication);

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserContextInitializationException(
                        BusinessErrorCode.USER_PROFILE_NOT_INITIALIZED,
                        user.getId(),
                        "user_profile"
                ));

        deleteExistingAvatarIfAny(profile);

        return new StoredAvatarBllDto(
                null,
                null
        );
    }

    private void deleteExistingAvatarIfAny(UserProfile profile) {
        if (profile.getAvatarFile() == null) {
            return;
        }

        UserFile avatarFile = profile.getAvatarFile();

        fileStorageService.delete(avatarFile.getStorageKey());

        avatarFile.softDelete();
        userFileRepository.save(avatarFile);

        profile.removeAvatar();
        userProfileRepository.save(profile);
    }

    private void validateAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file cannot be empty.");
        }

        if (file.getSize() > storageProperties.maxAvatarSizeBytes()) {
            throw new IllegalArgumentException("Avatar file is too large.");
        }

        String contentType = file.getContentType();

        if (!MIME_JPEG.equals(contentType)
                && !MIME_PNG.equals(contentType)
                && !MIME_WEBP.equals(contentType)) {
            throw new IllegalArgumentException("Unsupported avatar mime type.");
        }
    }

    private String resolveExtension(String mimeType) {
        return switch (mimeType) {
            case MIME_JPEG -> "jpg";
            case MIME_PNG -> "png";
            case MIME_WEBP -> "webp";
            default -> throw new IllegalArgumentException("Unsupported avatar mime type.");
        };
    }
}