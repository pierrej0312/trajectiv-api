package com.trajectiv.bll.models.careerai;

import java.util.List;

public record ArtifactValidationResult(
        boolean valid,
        List<String> errors
) {
    public ArtifactValidationResult {
        errors = List.copyOf(errors == null ? List.of() : errors);
        if (valid && !errors.isEmpty()) {
            throw new IllegalArgumentException("a valid result cannot contain errors");
        }
    }

    public static ArtifactValidationResult success() {
        return new ArtifactValidationResult(true, List.of());
    }

    public static ArtifactValidationResult failure(List<String> errors) {
        return new ArtifactValidationResult(false, errors);
    }
}