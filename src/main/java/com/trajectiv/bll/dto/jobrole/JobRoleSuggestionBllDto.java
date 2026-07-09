package com.trajectiv.bll.dto.jobrole;

import java.util.List;
import java.util.UUID;

public record JobRoleSuggestionBllDto(
        UUID id,
        String slug,
        String label,
        String description,
        JobRoleFamilyBll family,
        List<String> tags,
        List<String> aliases,
        int sortOrder,
        int score
) {
}