package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.opportunity.matching.MatchResumeOpportunityRequestApiDto;
import com.trajectiv.api.dto.me.opportunity.matching.ResumeOpportunityMatchResponseApiDto;
import com.trajectiv.bll.dto.opportunity.matching.MatchResumeOpportunityBllCommand;
import com.trajectiv.bll.dto.opportunity.matching.ResumeOpportunityMatchBllDto;
import org.springframework.stereotype.Component;

@Component
public class ResumeOpportunityMatchApiMapper {

    public MatchResumeOpportunityBllCommand toBllCommand(
            MatchResumeOpportunityRequestApiDto request
    ) {
        return new MatchResumeOpportunityBllCommand(
                request.resumeAnalysisId(),
                request.resumeAnalysis(),
                request.opportunityAnalysisId(),
                request.opportunityAnalysis()
        );
    }

    public ResumeOpportunityMatchResponseApiDto toApiDto(
            ResumeOpportunityMatchBllDto source
    ) {
        return new ResumeOpportunityMatchResponseApiDto(
                source.matchId(),
                source.schemaVersion(),
                source.promptVersion(),
                source.provider(),
                source.model(),
                source.durationMillis(),
                source.artifact()
        );
    }
}
