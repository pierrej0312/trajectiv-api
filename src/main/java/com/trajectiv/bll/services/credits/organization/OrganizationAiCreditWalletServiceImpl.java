package com.trajectiv.bll.services.credits.organization;

import com.trajectiv.bll.dto.billing.EffectiveEntitlementBllDto;
import com.trajectiv.bll.dto.me.AiOperationType;
import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.BusinessException;
import com.trajectiv.bll.exceptions.OrganizationAiCreditWalletNotFoundException;
import com.trajectiv.bll.services.credits.policy.AiCreditCostPolicy;
import com.trajectiv.bll.services.credits.policy.AiCreditLimitPolicy;
import com.trajectiv.bll.services.entitlement.organization.OrganizationEntitlementService;
import com.trajectiv.dl.entities.credits.OrganizationAiCreditWallet;
import com.trajectiv.dl.enums.FeatureKey;
import com.trajectiv.dl.repositories.credits.OrganizationAiCreditWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationAiCreditWalletServiceImpl
        implements OrganizationAiCreditWalletService {

    private final OrganizationAiCreditWalletRepository repository;
    private final OrganizationEntitlementService entitlementService;
    private final AiCreditLimitPolicy limitPolicy;
    private final AiCreditCostPolicy costPolicy;
    private final Clock clock;

    @Override
    @Transactional
    public OrganizationAiCreditWallet getCurrent(
            UUID organizationId
    ) {
        OrganizationAiCreditWallet wallet =
                getWalletForUpdate(
                        organizationId
                );

        renewIfNecessary(
                wallet,
                organizationId
        );

        return wallet;
    }

    @Override
    @Transactional
    public OrganizationAiCreditWallet consume(
            UUID organizationId,
            AiOperationType operationType
    ) {
        OrganizationAiCreditWallet wallet =
                getWalletForUpdate(
                        organizationId
                );

        renewIfNecessary(
                wallet,
                organizationId
        );

        wallet.consume(
                costPolicy.costOf(operationType),
                LocalDate.now(clock)
        );

        return wallet;
    }

    private OrganizationAiCreditWallet
    getWalletForUpdate(
            UUID organizationId
    ) {
        return repository
                .findByOrganizationIdForUpdate(
                        organizationId
                )
                .orElseThrow(
                        () ->
                                new OrganizationAiCreditWalletNotFoundException(
                                        organizationId
                                )
                );
    }

    private void renewIfNecessary(
            OrganizationAiCreditWallet wallet,
            UUID organizationId
    ) {
        EffectiveEntitlementBllDto entitlement =
                entitlementService
                        .resolveForOrganization(
                                organizationId,
                                FeatureKey
                                        .AI_CREDITS_MONTHLY_LIMIT
                        );

        int monthlyLimit =
                limitPolicy.resolveMonthlyLimit(
                        entitlement
                );

        wallet.renewPeriodIfExpired(
                LocalDate.now(clock),
                monthlyLimit
        );
    }
}