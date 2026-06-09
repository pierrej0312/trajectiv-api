package com.trajectiv.bll.mappers;

import com.trajectiv.bll.dto.me.SubscriptionBllDto;
import com.trajectiv.dl.entities.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionBllMapper {

    public SubscriptionBllDto toDto(Subscription subscription) {
        return new SubscriptionBllDto(
                subscription.getPlan(),
                subscription.getStatus()
        );
    }
}
