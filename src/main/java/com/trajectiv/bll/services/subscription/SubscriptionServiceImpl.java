package com.trajectiv.bll.services.subscription;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.UserContextInitializationException;
import com.trajectiv.bll.services.subscription.policy.SubscriptionEntitlementPolicy;
import com.trajectiv.dl.entities.Subscription;
import com.trajectiv.dl.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionEntitlementPolicy entitlementPolicy;

    @Override
    @Transactional(readOnly = true)
    public Subscription getCurrentSubscription(UUID userId) {
        return subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new UserContextInitializationException(
                        BusinessErrorCode.USER_SUBSCRIPTION_NOT_INITIALIZED,
                        userId,
                        "subscription"
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public int resolveAiMonthlyLimit(UUID userId) {
        Subscription subscription = getCurrentSubscription(userId);

        return entitlementPolicy.resolveAiMonthlyLimit(
                subscription.getPlan(),
                subscription.getStatus()
        );
    }
}
