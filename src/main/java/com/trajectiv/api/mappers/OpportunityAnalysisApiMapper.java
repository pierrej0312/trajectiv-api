package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.opportunity.analysis.AnalyzeOpportunityRequestApiDto;
import com.trajectiv.api.dto.me.opportunity.analysis.OpportunityAnalysisResponseApiDto;
import com.trajectiv.bll.dto.opportunity.analysis.AnalyzeOpportunityBllCommand;
import com.trajectiv.bll.dto.opportunity.analysis.OpportunityAnalysisBllDto;
import org.springframework.stereotype.Component;

@Component
public class OpportunityAnalysisApiMapper {

    public AnalyzeOpportunityBllCommand toBllCommand(
            AnalyzeOpportunityRequestApiDto request
    ) {
        return new AnalyzeOpportunityBllCommand(request.description());
    }

    public OpportunityAnalysisResponseApiDto toApiDto(
            OpportunityAnalysisBllDto source
    ) {
        return new OpportunityAnalysisResponseApiDto(
                source.analysisId(),
                source.schemaVersion(),
                source.promptVersion(),
                source.provider(),
                source.model(),
                source.durationMillis(),
                source.artifact()
        );
    }
}
