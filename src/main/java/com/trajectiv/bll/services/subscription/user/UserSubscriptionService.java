package com.trajectiv.bll.services.subscription.user;

import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.billing.UserSubscription;

import java.util.UUID;

public interface UserSubscriptionService {

    UserSubscription getCurrent(
            UUID userId
    );

    UserSubscription createFreeIfMissing(
            User user
    );
}