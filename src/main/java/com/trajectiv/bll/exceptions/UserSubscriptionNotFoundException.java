package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class UserSubscriptionNotFoundException
        extends BusinessException {

    public UserSubscriptionNotFoundException(
            UUID userId
    ) {
        super(
                BusinessErrorCode
                        .USER_SUBSCRIPTION_NOT_FOUND,
                "No subscription was found for user "
                        + userId + "."
        );
    }
}