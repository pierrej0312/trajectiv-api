package com.trajectiv.bll.services.subscription;

import com.trajectiv.dl.entities.Subscription;

import java.util.UUID;

public interface SubscriptionService {
    Subscription getCurrentSubscription(UUID userId);

    int resolveAiMonthlyLimit(UUID userId);

    /*
    boolean canUsePremiumFeature(UUID userId);

    boolean canExportResume(UUID userId);

    int resolveResumeLimit(UUID userId);

    void synchronizeSubscription(UUID userId);
     */
}
