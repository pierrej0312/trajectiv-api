package com.trajectiv.bll.exceptions.opportunity;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.BusinessException;

public class InvalidResumeOpportunityMatchException
        extends BusinessException {

    private final String validationDetails;

    public InvalidResumeOpportunityMatchException(
            String validationDetails
    ) {
        super(
                BusinessErrorCode.RESUME_OPPORTUNITY_MATCH_INVALID,
                validationDetails
        );
        this.validationDetails = validationDetails;
    }

    public String getValidationDetails() {
        return validationDetails;
    }
}