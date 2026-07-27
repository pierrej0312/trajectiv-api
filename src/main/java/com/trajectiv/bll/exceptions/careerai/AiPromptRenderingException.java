package com.trajectiv.bll.exceptions.careerai;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.BusinessException;

public class AiPromptRenderingException extends BusinessException {

    public AiPromptRenderingException(String message) {
        super(
                BusinessErrorCode.AI_PROMPT_RENDERING_FAILED,
                message
        );
    }

    public AiPromptRenderingException(
            String message,
            Throwable cause
    ) {
        super(
                BusinessErrorCode.AI_PROMPT_RENDERING_FAILED,
                message,
                cause
        );
    }
}