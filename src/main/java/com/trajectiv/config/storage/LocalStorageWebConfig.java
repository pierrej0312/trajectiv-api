package com.trajectiv.config.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition
        .ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation
        .ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation
        .WebMvcConfigurer;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "storage",
        name = "provider",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalStorageWebConfig
        implements WebMvcConfigurer {

    private static final String FILES_PATTERN =
            "/files/**";

    private final StorageProperties storageProperties;

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {
        Path storageRoot = resolveStorageRoot();

        registry
                .addResourceHandler(FILES_PATTERN)
                .addResourceLocations(
                        storageRoot
                                .toUri()
                                .toString()
                );
    }

    private Path resolveStorageRoot() {
        String localRoot =
                storageProperties.requireLocalRoot();

        try {
            return Path.of(localRoot)
                    .toAbsolutePath()
                    .normalize();
        } catch (InvalidPathException exception) {
            throw new IllegalStateException(
                    "Invalid storage.local-root path: "
                            + localRoot,
                    exception
            );
        }
    }
}