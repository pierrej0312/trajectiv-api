package com.trajectiv.bll.dto.resume.analysis;

import java.util.Objects;

public record AnalyzeResumeBllCommand(
        String fileName,
        String contentType,
        byte[] content
) {
    public AnalyzeResumeBllCommand {
        fileName = Objects.requireNonNull(fileName, "fileName is required").trim();
        contentType = contentType == null ? "application/octet-stream" : contentType.trim();
        content = Objects.requireNonNull(content, "content is required").clone();

        if (fileName.isBlank()) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
        if (content.length == 0) {
            throw new IllegalArgumentException("content must not be empty");
        }
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
