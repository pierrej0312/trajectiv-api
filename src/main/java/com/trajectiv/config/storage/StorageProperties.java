package com.trajectiv.config.storage;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
        StorageProvider provider,
        String localRoot,
        String publicBaseUrl,
        long maxAvatarSizeBytes
) {
}
