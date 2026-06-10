package com.trajectiv.bll.dto.storage;

public record StoredFileBllDto(
        String storageKey,
        String publicUrl,
        String originalFilename,
        String mimeType,
        long sizeBytes
) {
}
