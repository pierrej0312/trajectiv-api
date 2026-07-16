package com.trajectiv.bll.services.subscription;

import com.trajectiv.bll.dto.me.workspace.WorkspacePlanBllDto;
import com.trajectiv.bll.exceptions.PlanNotFoundException;
import com.trajectiv.dl.enums.billing.SubscriptionStatus;
import com.trajectiv.dl.repositories.billing.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WorkspacePlanResolver {

    private final PlanRepository planRepository;

    @Transactional(readOnly = true)
    public WorkspacePlanBllDto resolve(
            String planCode,
            SubscriptionStatus status
    ) {
        var plan = planRepository
                .findByCodeAndActiveTrue(planCode)
                .orElseThrow(
                        () ->
                                new PlanNotFoundException(
                                        planCode
                                )
                );

        return new WorkspacePlanBllDto(
                plan.getCode(),
                plan.getLabel(),
                status
        );
    }
}