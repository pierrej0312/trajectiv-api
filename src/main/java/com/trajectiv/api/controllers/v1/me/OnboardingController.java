package com.trajectiv.api.controllers.v1.me;


import com.trajectiv.api.dto.me.MeOnboardingApiDto;
import com.trajectiv.api.mappers.MeApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.me.MeOnboardingBllDto;
import com.trajectiv.bll.services.onboarding.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.V1.ME_ONBOARDING)
@RequiredArgsConstructor
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final MeApiMapper meApiMapper;

    @PostMapping("/complete")
    @Operation(summary = "Complete current user onboarding")
    public MeOnboardingApiDto completeOnboarding(Authentication authentication) {
        MeOnboardingBllDto onboarding = onboardingService.completeCurrentUserOnboarding(authentication);

        return meApiMapper.toOnboardingApiDto(onboarding);
    }
}
