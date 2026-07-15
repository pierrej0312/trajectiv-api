package com.trajectiv.bll.services.credits.policy;

import com.trajectiv.bll.dto.me.AiOperationType;
import org.springframework.stereotype.Component;

@Component
public class AiCreditCostPolicy {

    public int costOf(AiOperationType operationType) {
        return switch (operationType) {
            case INTERVIEW_ANSWER_EVALUATION -> 1;
            case COVER_LETTER_GENERATION -> 1;
            case JOB_MATCH_ANALYSIS -> 2;
            case CV_ANALYSIS -> 3;
        };
    }
}
