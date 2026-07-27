package com.trajectiv.bll.dto.storage;

import java.util.Objects;

public record ExtractedDocumentPageBllDto(
        int pageNumber,
        String text
) {
    public ExtractedDocumentPageBllDto {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than zero");
        }
        text = Objects.requireNonNullElse(text, "");
    }
}