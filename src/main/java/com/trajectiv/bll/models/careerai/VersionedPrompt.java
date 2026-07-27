package com.trajectiv.bll.models.careerai;

import java.util.Objects;

public record VersionedPrompt(
        String id,
        String version,
        String content
) {
    public VersionedPrompt {
        id = Objects.requireNonNull(id, "id is required");
        version = Objects.requireNonNull(version, "version is required");
        content = Objects.requireNonNull(content, "content is required");
        if (content.isBlank()) {
            throw new IllegalArgumentException("prompt content must not be blank");
        }
    }
}