package com.trajectiv.bll.services.opportunity.matching;

import com.trajectiv.bll.dto.opportunity.matching.MatchResumeOpportunityBllCommand;
import com.trajectiv.bll.dto.opportunity.matching.ResumeOpportunityMatchBllDto;

public interface ResumeOpportunityMatchService {

    ResumeOpportunityMatchBllDto match(MatchResumeOpportunityBllCommand command);
}