package com.trajectiv.bll.services.careerai.provider;

import com.trajectiv.bll.exceptions.careerai.AiResourceNotFoundException;
import com.trajectiv.bll.models.careerai.AiProviderRequest;
import com.trajectiv.bll.models.careerai.AiProviderResponse;
import com.trajectiv.bll.models.careerai.AiUseCaseDefinition;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Profile("demo-ai")
public final class DemoAiProviderImpl implements  AiProvider {

    private final ResourceLoader resourceLoader;
    private final ConcurrentMap<String, String> fixtureCache = new ConcurrentHashMap<>();

    public DemoAiProviderImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public AiProviderResponse generate(AiProviderRequest request) {
        long start = System.nanoTime();
        AiUseCaseDefinition definition = AiUseCaseDefinition.forUseCase(request.useCase());
        String fixture = fixtureCache.computeIfAbsent(
                definition.fixtureResource(),
                this::loadFixture
        );
        long durationMillis = (System.nanoTime() - start) / 1_000_000;

        return new AiProviderResponse(
                "DEMO_FIXTURE",
                "fixture:" + request.useCase().name(),
                fixture,
                durationMillis
        );
    }

    private String loadFixture(String location) {
        Resource resource = resourceLoader.getResource(location);
        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new AiResourceNotFoundException(location, exception);
        }
    }
}
