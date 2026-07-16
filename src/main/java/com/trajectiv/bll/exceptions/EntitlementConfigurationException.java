package com.trajectiv.bll.exceptions;

public class EntitlementConfigurationException
        extends BusinessException {

    public EntitlementConfigurationException(
            String message
    ) {
        super(
                BusinessErrorCode
                        .PLAN_ENTITLEMENT_CONFIGURATION_INVALID,
                message
        );
    }
}