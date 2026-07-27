package com.trajectiv.bll.services.careerai.provider;

import com.trajectiv.bll.models.careerai.AiProviderRequest;
import com.trajectiv.bll.models.careerai.AiProviderResponse;

public interface AiProvider {

    AiProviderResponse generate(AiProviderRequest request);
}
