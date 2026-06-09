package com.trajectiv.bll.services.me;

import com.trajectiv.bll.exceptions.InvalidAuthenticatedUserClaimsException;
import com.trajectiv.config.security.AuthenticatedUserClaims;
import com.trajectiv.config.security.AuthenticatedUserProvider;
import com.trajectiv.dl.entities.AiCreditWallet;
import com.trajectiv.dl.entities.Subscription;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserProfile;
import com.trajectiv.dl.repositories.AiCreditWalletRepository;
import com.trajectiv.dl.repositories.SubscriptionRepository;
import com.trajectiv.dl.repositories.UserProfileRepository;
import com.trajectiv.dl.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSyncServiceImpl implements UserSyncService {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AiCreditWalletRepository aiCreditWalletRepository;

    @Override
    @Transactional
    public User syncFromAuthentication(Authentication authentication) {
        AuthenticatedUserClaims claims = authenticatedUserProvider.getClaims(authentication);

        return syncFromKeycloakClaims(claims);
    }

    @Override
    @Transactional
    public User syncFromKeycloakClaims(AuthenticatedUserClaims claims) {
        validateClaims(claims);

        User user = userRepository.findByKeycloakSubject(claims.keycloakSubject())
                .map(existingUser -> updateExistingUser(existingUser, claims))
                .orElseGet(() -> createNewUser(claims));

        ensureDefaultProfile(user);
        ensureDefaultSubscription(user);
        ensureDefaultAiCreditWallet(user);

        return user;
    }

    private User createNewUser(AuthenticatedUserClaims claims) {
        User user = User.createFromKeycloak(
                claims.keycloakSubject(),
                claims.email(),
                claims.emailVerified(),
                claims.firstName(),
                claims.lastName(),
                resolveDisplayName(claims)
        );

        return userRepository.save(user);
    }

    private User updateExistingUser(User user, AuthenticatedUserClaims claims) {
        user.updateFromKeycloak(
                claims.email(),
                claims.emailVerified(),
                claims.firstName(),
                claims.lastName(),
                resolveDisplayName(claims)
        );

        return userRepository.save(user);
    }

    private void ensureDefaultProfile(User user) {
        if (userProfileRepository.existsByUserId(user.getId())) {
            return;
        }

        UserProfile profile = UserProfile.createDefault(user);
        userProfileRepository.save(profile);
    }

    private void ensureDefaultSubscription(User user) {
        if (subscriptionRepository.existsByUserId(user.getId())) {
            return;
        }

        Subscription subscription = Subscription.createFree(user);
        subscriptionRepository.save(subscription);
    }

    private void ensureDefaultAiCreditWallet(User user) {
        if (aiCreditWalletRepository.existsByUserId(user.getId())) {
            return;
        }

        AiCreditWallet wallet = AiCreditWallet.createFreeDefault(user);
        aiCreditWalletRepository.save(wallet);
    }

    private void validateClaims(AuthenticatedUserClaims claims) {
        if (claims == null) {
            throw new InvalidAuthenticatedUserClaimsException("Authenticated user claims are required.");
        }

        if (claims.keycloakSubject() == null || claims.keycloakSubject().isBlank()) {
            throw new InvalidAuthenticatedUserClaimsException("Keycloak subject is required.");
        }

        if (claims.email() == null || claims.email().isBlank()) {
            throw new InvalidAuthenticatedUserClaimsException("Email is required.");
        }
    }

    private String resolveDisplayName(AuthenticatedUserClaims claims) {
        String fullName = joinNonBlank(claims.firstName(), claims.lastName());

        if (!fullName.isBlank()) {
            return fullName;
        }

        if (claims.preferredUsername() != null && !claims.preferredUsername().isBlank()) {
            return claims.preferredUsername();
        }

        return claims.email();
    }

    private String joinNonBlank(String firstName, String lastName) {
        String safeFirstName = firstName == null ? "" : firstName.trim();
        String safeLastName = lastName == null ? "" : lastName.trim();

        return (safeFirstName + " " + safeLastName).trim();
    }

}
