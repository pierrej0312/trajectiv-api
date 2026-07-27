package com.trajectiv.bll.services.careerai.prompt;

import com.trajectiv.bll.models.careerai.AiUseCase;
import com.trajectiv.bll.models.careerai.VersionedPrompt;

public interface AiPromptRegistry {

    VersionedPrompt get(AiUseCase useCase);
}
