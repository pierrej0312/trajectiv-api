package com.trajectiv.bll.services.me;

import com.trajectiv.config.security.AuthenticatedUserClaims;
import com.trajectiv.dl.entities.User;
import org.springframework.security.core.Authentication;

public interface UserSyncService {

    User syncFromAuthentication(Authentication authentication);

    User syncFromKeycloakClaims(AuthenticatedUserClaims claims);
}
