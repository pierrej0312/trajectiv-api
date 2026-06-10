package com.trajectiv.bll.services.storage;


import com.trajectiv.bll.dto.storage.StorageCommandBllDto;
import com.trajectiv.bll.dto.storage.StoredFileBllDto;
import com.trajectiv.config.storage.StorageProperties;
import com.trajectiv.config.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "storage",
        name = "provider",
        havingValue = "local",
        matchIfMissing = true
)
public class FileStorageServiceImpl implements FileStorageService {

    private final StorageProperties storageProperties;

    @Override
    public StoredFileBllDto store(StorageCommandBllDto command) {
        validateLocalProvider();

        String safeDirectory = normalizeStoragePath(command.directory());
        String safeFilename = normalizeStoragePath(command.targetFilename());

        String storageKey = safeDirectory + "/" + safeFilename;

        Path rootPath = Path.of(storageProperties.localRoot()).toAbsolutePath().normalize();
        Path targetPath = rootPath.resolve(storageKey).normalize();

        ensureTargetIsInsideRoot(rootPath, targetPath);

        try {
            Files.createDirectories(targetPath.getParent());

            try (InputStream inputStream = command.inputStream()) {
                Files.copy(inputStream, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            return new StoredFileBllDto(
                    storageKey,
                    buildPublicUrl(storageKey),
                    command.originalFilename(),
                    command.mimeType(),
                    command.sizeBytes()
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Could not store file locally.", exception);
        }
    }

    @Override
    public void delete(String storageKey) {
        validateLocalProvider();

        String safeStorageKey = normalizeStoragePath(storageKey);

        Path rootPath = Path.of(storageProperties.localRoot()).toAbsolutePath().normalize();
        Path targetPath = rootPath.resolve(safeStorageKey).normalize();

        ensureTargetIsInsideRoot(rootPath, targetPath);

        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not delete local file.", exception);
        }
    }

    private void validateLocalProvider() {
        if (storageProperties.provider() != StorageProvider.LOCAL) {
            throw new IllegalStateException("Local storage service cannot be used with provider " + storageProperties.provider());
        }
    }

    private String buildPublicUrl(String storageKey) {
        String baseUrl = storageProperties.publicBaseUrl();

        if (baseUrl.endsWith("/")) {
            return baseUrl + storageKey;
        }

        return baseUrl + "/" + storageKey;
    }

    private String normalizeStoragePath(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Storage path cannot be blank.");
        }

        String normalized = value
                .replace("\\", "/")
                .replaceAll("/{2,}", "/");

        if (normalized.contains("..")) {
            throw new IllegalArgumentException("Storage path cannot contain parent traversal.");
        }

        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        return normalized;
    }

    private void ensureTargetIsInsideRoot(Path rootPath, Path targetPath) {
        if (!targetPath.startsWith(rootPath)) {
            throw new IllegalArgumentException("Resolved file path is outside storage root.");
        }
    }
}
