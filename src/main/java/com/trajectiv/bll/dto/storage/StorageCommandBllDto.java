package com.trajectiv.bll.dto.storage;

import java.io.InputStream;
import java.util.UUID;

public record StorageCommandBllDto(
        UUID userId,
        String directory,
        String targetFilename,
        String originalFilename,
        String mimeType,
        long sizeBytes,
        InputStream inputStream
) {
}
