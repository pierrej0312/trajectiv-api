package com.trajectiv.bll.services.careerai.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.trajectiv.bll.models.careerai.ArtifactValidationResult;

public interface AiArtifactValidator {

    ArtifactValidationResult validate(
            String schemaVersion,
            JsonNode schema,
            JsonNode artifact
    );
}
