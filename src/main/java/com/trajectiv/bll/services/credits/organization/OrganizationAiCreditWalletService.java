package com.trajectiv.bll.services.credits.organization;

import com.trajectiv.bll.dto.me.AiOperationType;
import com.trajectiv.dl.entities.credits.OrganizationAiCreditWallet;

import java.util.UUID;

public interface OrganizationAiCreditWalletService {

    OrganizationAiCreditWallet getCurrent(
            UUID organizationId
    );

    OrganizationAiCreditWallet consume(
            UUID organizationId,
            AiOperationType operationType
    );
}