package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.subscription.MeSubscriptionApiDto;
import com.trajectiv.api.dto.me.subscription.SubscriptionStatusApiDto;
import com.trajectiv.bll.dto.billing.UserSubscriptionBllDto;
import com.trajectiv.dl.enums.billing.SubscriptionStatus;
import org.springframework.stereotype.Component;

@Component
public class MeSubscriptionApiMapper {

    public MeSubscriptionApiDto toApiDto(
            UserSubscriptionBllDto subscription
    ) {
        return new MeSubscriptionApiDto(
                subscription.planCode(),
                mapStatus(subscription.status()),
                subscription.currentPeriodEnd(),
                subscription.cancelAtPeriodEnd(),
                subscription.trialEnd()
        );
    }

    private SubscriptionStatusApiDto mapStatus(
            SubscriptionStatus status
    ) {
        return switch (status) {
            case ACTIVE ->
                    SubscriptionStatusApiDto.ACTIVE;

            case TRIALING ->
                    SubscriptionStatusApiDto.TRIALING;

            case PAST_DUE ->
                    SubscriptionStatusApiDto.PAST_DUE;

            case CANCELED ->
                    SubscriptionStatusApiDto.CANCELED;

            case EXPIRED ->
                    SubscriptionStatusApiDto.EXPIRED;
        };
    }
}