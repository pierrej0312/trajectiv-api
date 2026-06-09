package com.trajectiv.bll.services.me;

import com.trajectiv.bll.dto.me.MeBllDto;
import org.springframework.security.core.Authentication;

public interface MeService {
    MeBllDto getMe(Authentication authentication);
}
