package com.trajectiv.bll.exceptions;

import com.trajectiv.bll.dto.me.onboarding.OnboardingMissingField;

import java.util.List;

public class OnboardingRequiredFieldsMissingException extends BusinessException {
    private final List<OnboardingMissingField> missingFields;

    public OnboardingRequiredFieldsMissingException(List<OnboardingMissingField> missingFields) {
        super(
                BusinessErrorCode.ONBOARDING_REQUIRED_FIELDS_MISSING,
                "Onboarding cannot be completed because required fields are missing."
        );
        this.missingFields = missingFields;
    }

    public List<OnboardingMissingField> getMissingFields() {
        return missingFields;
    }
}
