package com.trajectiv.bll.services.me.sync;

import com.trajectiv.bll.exceptions.InvalidAuthenticatedUserClaimsException;
import com.trajectiv.bll.services.credits.user.initialization.UserAiCreditWalletInitializer;
import com.trajectiv.bll.services.subscription.user.UserSubscriptionService;
import com.trajectiv.config.security.AuthenticatedUserClaims;
import com.trajectiv.config.security.AuthenticatedUserProvider;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.repositories.UserProfileRepository;
import com.trajectiv.dl.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSyncServiceImpl
        implements UserSyncService {

    private final AuthenticatedUserProvider
            authenticatedUserProvider;

    private final UserRepository
            userRepository;

    private final UserProfileRepository
            userProfileRepository;

    private final UserSubscriptionService
            userSubscriptionService;

    private final UserAiCreditWalletInitializer
            userAiCreditWalletInitializer;

    @Override
    @Transactional
    public User syncFromAuthentication(
            Authentication authentication
    ) {
        AuthenticatedUserClaims claims =
                authenticatedUserProvider.getClaims(
                        authentication
                );

        return syncFromKeycloakClaims(claims);
    }

    @Override
    @Transactional
    public User syncFromKeycloakClaims(
            AuthenticatedUserClaims claims
    ) {
        validateClaims(claims);

        User user = userRepository
                .findByKeycloakSubject(
                        claims.keycloakSubject()
                )
                .map(
                        existingUser ->
                                updateExistingUser(
                                        existingUser,
                                        claims
                                )
                )
                .orElseGet(
                        () -> createNewUser(claims)
                );

        initializeUserContext(user);

        return user;
    }

    private User createNewUser(
            AuthenticatedUserClaims claims
    ) {
        User user = User.createFromKeycloak(
                claims.keycloakSubject(),
                normalizeEmail(claims.email()),
                claims.emailVerified(),
                normalizeNullableText(
                        claims.firstName()
                ),
                normalizeNullableText(
                        claims.lastName()
                ),
                resolveDisplayName(claims)
        );

        return userRepository.save(user);
    }

    private User updateExistingUser(
            User user,
            AuthenticatedUserClaims claims
    ) {
        user.updateFromKeycloak(
                normalizeEmail(claims.email()),
                claims.emailVerified(),
                normalizeNullableText(
                        claims.firstName()
                ),
                normalizeNullableText(
                        claims.lastName()
                ),
                resolveDisplayName(claims)
        );

        /*
         * L'entité est managée dans la transaction.
         * Un save explicite n'est normalement pas nécessaire.
         */
        return user;
    }

    private void initializeUserContext(
            User user
    ) {
        createDefaultProfileIfMissing(user);

        userSubscriptionService
                .createFreeIfMissing(user);

        userAiCreditWalletInitializer
                .createDefaultIfMissing(user);
    }

    private void createDefaultProfileIfMissing(
            User user
    ) {
        if (
                userProfileRepository.existsByUserId(
                        user.getId()
                )
        ) {
            return;
        }

        UserProfile profile =
                UserProfile.createDefault(user);

        userProfileRepository.save(profile);
    }

    private void validateClaims(
            AuthenticatedUserClaims claims
    ) {
        if (claims == null) {
            throw new InvalidAuthenticatedUserClaimsException(
                    "Authenticated user claims are required."
            );
        }

        if (
                claims.keycloakSubject() == null ||
                        claims.keycloakSubject().isBlank()
        ) {
            throw new InvalidAuthenticatedUserClaimsException(
                    "Keycloak subject is required."
            );
        }

        if (
                claims.email() == null ||
                        claims.email().isBlank()
        ) {
            throw new InvalidAuthenticatedUserClaimsException(
                    "Email is required."
            );
        }
    }

    private String resolveDisplayName(
            AuthenticatedUserClaims claims
    ) {
        String fullName = joinNonBlank(
                claims.firstName(),
                claims.lastName()
        );

        if (!fullName.isBlank()) {
            return fullName;
        }

        String preferredUsername =
                normalizeNullableText(
                        claims.preferredUsername()
                );

        if (preferredUsername != null) {
            return preferredUsername;
        }

        return normalizeEmail(claims.email());
    }

    private String joinNonBlank(
            String firstName,
            String lastName
    ) {
        String safeFirstName =
                normalizeNullableText(firstName);

        String safeLastName =
                normalizeNullableText(lastName);

        return (
                (safeFirstName == null
                        ? ""
                        : safeFirstName)
                        + " "
                        + (safeLastName == null
                        ? ""
                        : safeLastName)
        ).trim();
    }

    private String normalizeEmail(
            String email
    ) {
        return email
                .trim()
                .toLowerCase();
    }

    private String normalizeNullableText(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}