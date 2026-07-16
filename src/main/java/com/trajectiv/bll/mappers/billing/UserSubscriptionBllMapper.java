package com.trajectiv.bll.mappers.billing;

import com.trajectiv.bll.dto.billing.UserSubscriptionBllDto;
import com.trajectiv.dl.entities.billing.UserSubscription;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserSubscriptionBllMapper {

    public UserSubscriptionBllDto toDto(
            UserSubscription entity,
            UUID userId
    ) {
        return new UserSubscriptionBllDto(
                entity.getId(),
                userId,
                entity.getPlanCode(),
                entity.getStatus(),
                entity.getCurrentPeriodStart(),
                entity.getCurrentPeriodEnd(),
                entity.isCancelAtPeriodEnd(),
                entity.getTrialEnd()
        );
    }
}