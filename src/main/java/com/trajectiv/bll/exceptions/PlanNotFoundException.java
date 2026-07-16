package com.trajectiv.bll.exceptions;

public class PlanNotFoundException
        extends BusinessException {

    public PlanNotFoundException(
            String planCode
    ) {
        super(
                BusinessErrorCode.PLAN_NOT_FOUND,
                "No active plan was found for code "
                        + planCode + "."
        );
    }
}