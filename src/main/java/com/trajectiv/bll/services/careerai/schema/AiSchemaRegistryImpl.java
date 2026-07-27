package com.trajectiv.bll.services.careerai.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trajectiv.bll.exceptions.careerai.AiResourceNotFoundException;
import com.trajectiv.bll.models.careerai.AiUseCase;
import com.trajectiv.bll.models.careerai.AiUseCaseDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AiSchemaRegistryImpl implements AiSchemaRegistry {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<AiUseCase, JsonNode> cache = new ConcurrentHashMap<>();

    public AiSchemaRegistryImpl(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getSchemaVersion(AiUseCase useCase) {
        return AiUseCaseDefinition.forUseCase(useCase).schemaVersion();
    }

    @Override
    public JsonNode getSchema(AiUseCase useCase) {
        return cache.computeIfAbsent(useCase, this::loadSchema).deepCopy();
    }

    private JsonNode loadSchema(AiUseCase useCase) {
        AiUseCaseDefinition definition = AiUseCaseDefinition.forUseCase(useCase);
        Resource resource = resourceLoader.getResource(definition.schemaResource());

        try (var inputStream = resource.getInputStream()) {
            return objectMapper.readTree(inputStream);
        } catch (IOException exception) {
            throw new AiResourceNotFoundException(definition.schemaResource(), exception);
        }
    }
}
