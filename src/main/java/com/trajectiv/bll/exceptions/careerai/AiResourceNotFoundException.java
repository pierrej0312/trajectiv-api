package com.trajectiv.bll.exceptions.careerai;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.BusinessException;
import lombok.Getter;

@Getter
public class AiResourceNotFoundException extends BusinessException {

    private final String resourceLocation;

    public AiResourceNotFoundException(String resourceLocation) {
        this(resourceLocation, null);
    }

    public AiResourceNotFoundException(
            String resourceLocation,
            Throwable cause
    ) {
        super(
                BusinessErrorCode.AI_RESOURCE_NOT_FOUND,
                "AI resource could not be loaded: " + resourceLocation,
                cause
        );
        this.resourceLocation = resourceLocation;
    }

}