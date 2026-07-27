package com.trajectiv.bll.dto.storage;

import java.util.List;

public record ExtractedDocumentTextBllDto(
        int pageCount,
        boolean textExtractable,
        List<ExtractedDocumentPageBllDto> pages,
        List<String> warnings
) {
    public ExtractedDocumentTextBllDto {
        if (pageCount < 0) {
            throw new IllegalArgumentException("pageCount must not be negative");
        }
        pages = List.copyOf(pages == null ? List.of() : pages);
        warnings = List.copyOf(warnings == null ? List.of() : warnings);
    }
}