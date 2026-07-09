package com.trajectiv.api.dto.jobRole;

import java.util.List;
import java.util.UUID;

public record JobRoleSuggestionApiDto(
        UUID id,
        String slug,
        String label,
        String description,
        JobRoleFamilyApiDto family,
        List<String> tags,
        List<String> aliases,
        int sortOrder,
        int score
) {
}
