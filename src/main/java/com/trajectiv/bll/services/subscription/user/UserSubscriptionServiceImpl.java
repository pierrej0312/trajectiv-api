package com.trajectiv.bll.services.subscription.user;

import com.trajectiv.bll.exceptions.UserSubscriptionNotFoundException;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.billing.UserSubscription;
import com.trajectiv.dl.repositories.billing.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl
        implements UserSubscriptionService {

    private final UserSubscriptionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserSubscription getCurrent(
            UUID userId
    ) {
        Objects.requireNonNull(
                userId,
                "userId cannot be null."
        );

        return repository
                .findByUserId(userId)
                .orElseThrow(
                        () ->
                                new UserSubscriptionNotFoundException(
                                        userId
                                )
                );
    }

    @Override
    @Transactional
    public UserSubscription createFreeIfMissing(
            User user
    ) {
        Objects.requireNonNull(
                user,
                "user cannot be null."
        );

        return repository
                .findByUserId(user.getId())
                .orElseGet(
                        () -> repository.save(
                                UserSubscription.createFree(
                                        user
                                )
                        )
                );
    }
}