package com.trajectiv.bll.services.careerai.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.trajectiv.bll.models.careerai.AiUseCase;

public interface AiSchemaRegistry {

    String getSchemaVersion(AiUseCase useCase);

    JsonNode getSchema(AiUseCase useCase);
}