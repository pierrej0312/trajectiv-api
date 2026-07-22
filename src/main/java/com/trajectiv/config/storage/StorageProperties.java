package com.trajectiv.config.storage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(
        prefix = "storage"
)
public record StorageProperties(
        @NotNull
        StorageProvider provider,

        String localRoot,

        String publicBaseUrl,

        @Min(1)
        long maxAvatarSizeBytes
) {

    public String requireLocalRoot() {
        if (
                provider == StorageProvider.LOCAL
                        && (
                        localRoot == null
                                || localRoot.isBlank()
                )
        ) {
            throw new IllegalStateException(
                    """
                    storage.local-root must be configured \
                    when storage.provider=local.
                    """
            );
        }

        return localRoot;
    }

    public String requirePublicBaseUrl() {
        if (
                publicBaseUrl == null
                        || publicBaseUrl.isBlank()
        ) {
            throw new IllegalStateException(
                    "storage.public-base-url must be configured."
            );
        }

        return publicBaseUrl;
    }
}