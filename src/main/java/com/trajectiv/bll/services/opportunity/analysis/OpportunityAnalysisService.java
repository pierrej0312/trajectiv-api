package com.trajectiv.bll.services.opportunity.analysis;

import com.trajectiv.bll.dto.opportunity.analysis.AnalyzeOpportunityBllCommand;
import com.trajectiv.bll.dto.opportunity.analysis.OpportunityAnalysisBllDto;

public interface OpportunityAnalysisService {

    OpportunityAnalysisBllDto analyze(AnalyzeOpportunityBllCommand command);
}