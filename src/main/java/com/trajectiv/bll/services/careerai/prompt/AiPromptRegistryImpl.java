package com.trajectiv.bll.services.careerai.prompt;

import com.trajectiv.bll.exceptions.careerai.AiResourceNotFoundException;
import com.trajectiv.bll.models.careerai.AiUseCase;
import com.trajectiv.bll.models.careerai.AiUseCaseDefinition;
import com.trajectiv.bll.models.careerai.VersionedPrompt;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AiPromptRegistryImpl implements AiPromptRegistry {

    private final ResourceLoader resourceLoader;
    private final ConcurrentMap<AiUseCase, VersionedPrompt> cache = new ConcurrentHashMap<>();

    public AiPromptRegistryImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public VersionedPrompt get(AiUseCase useCase) {
        return cache.computeIfAbsent(useCase, this::loadPrompt);
    }

    private VersionedPrompt loadPrompt(AiUseCase useCase) {
        AiUseCaseDefinition definition = AiUseCaseDefinition.forUseCase(useCase);
        Resource resource = resourceLoader.getResource(definition.promptResource());

        try (var inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return new VersionedPrompt(
                    definition.promptId(),
                    definition.promptVersion(),
                    content
            );
        } catch (IOException exception) {
            throw new AiResourceNotFoundException(definition.promptResource(), exception);
        }
    }
}
