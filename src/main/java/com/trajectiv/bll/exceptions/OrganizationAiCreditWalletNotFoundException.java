package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class OrganizationAiCreditWalletNotFoundException
        extends BusinessException {

    public OrganizationAiCreditWalletNotFoundException(
            UUID organizationId
    ) {
        super(
                BusinessErrorCode
                        .ORGANIZATION_AI_CREDIT_WALLET_NOT_FOUND,
                "AI credit wallet was not found for organization "
                        + organizationId + "."
        );
    }
}