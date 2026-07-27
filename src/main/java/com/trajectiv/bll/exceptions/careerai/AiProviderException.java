package com.trajectiv.bll.exceptions.careerai;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.BusinessException;
import lombok.Getter;

@Getter
public final class AiProviderException extends BusinessException {

    private final String provider;

    private AiProviderException(
            BusinessErrorCode errorCode,
            String publicMessage,
            String provider,
            Throwable cause
    ) {
        super(
                errorCode,
                publicMessage,
                cause,
                true
        );
        this.provider = provider;
    }

    public static AiProviderException unavailable(
            String provider,
            Throwable cause
    ) {
        return new AiProviderException(
                BusinessErrorCode.AI_PROVIDER_UNAVAILABLE,
                "The AI analysis service is temporarily unavailable.",
                provider,
                cause
        );
    }

    public static AiProviderException timeout(
            String provider,
            Throwable cause
    ) {
        return new AiProviderException(
                BusinessErrorCode.AI_PROVIDER_TIMEOUT,
                "The AI analysis service did not respond in time.",
                provider,
                cause
        );
    }

    public static AiProviderException invalidResponse(
            String provider
    ) {
        return invalidResponse(provider, null);
    }

    public static AiProviderException invalidResponse(
            String provider,
            Throwable cause
    ) {
        return new AiProviderException(
                BusinessErrorCode.AI_PROVIDER_RESPONSE_INVALID,
                "The AI analysis service returned an invalid response.",
                provider,
                cause
        );
    }

}