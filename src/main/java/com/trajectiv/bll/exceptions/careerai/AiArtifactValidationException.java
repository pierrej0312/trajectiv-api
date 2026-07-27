package com.trajectiv.bll.exceptions.careerai;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.BusinessException;
import lombok.Getter;

import java.util.List;

@Getter
public class AiArtifactValidationException extends BusinessException {

    private final String schemaVersion;
    private final List<String> validationErrors;

    public AiArtifactValidationException(
            String schemaVersion,
            List<String> validationErrors
    ) {
        super(
                BusinessErrorCode.AI_ARTIFACT_VALIDATION_FAILED,
                "AI artifact does not conform to schema %s: %s".formatted(
                        schemaVersion,
                        String.join(
                                " | ",
                                validationErrors == null
                                        ? List.of()
                                        : validationErrors
                        )
                )
        );
        this.schemaVersion = schemaVersion;
        this.validationErrors = List.copyOf(
                validationErrors == null
                        ? List.of()
                        : validationErrors
        );
    }

}