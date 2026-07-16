package com.trajectiv.bll.mappers.billing;

import com.trajectiv.bll.dto.billing.OrganizationSubscriptionBllDto;
import com.trajectiv.dl.entities.billing.OrganizationSubscription;
import org.springframework.stereotype.Component;

@Component
public class OrganizationSubscriptionBllMapper {

    public OrganizationSubscriptionBllDto toDto(
            OrganizationSubscription entity
    ) {
        return new OrganizationSubscriptionBllDto(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getPlanCode(),
                entity.getStatus(),
                entity.getCurrentPeriodStart(),
                entity.getCurrentPeriodEnd(),
                entity.isCancelAtPeriodEnd(),
                entity.getSeatLimit()
        );
    }
}