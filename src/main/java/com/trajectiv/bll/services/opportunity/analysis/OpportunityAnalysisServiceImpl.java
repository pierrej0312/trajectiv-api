package com.trajectiv.bll.services.opportunity.analysis;

import com.trajectiv.bll.dto.careerai.AiExecutionCommandBllDto;
import com.trajectiv.bll.dto.careerai.AiExecutionResultBllDto;
import com.trajectiv.bll.dto.opportunity.analysis.AnalyzeOpportunityBllCommand;
import com.trajectiv.bll.dto.opportunity.analysis.OpportunityAnalysisBllDto;
import com.trajectiv.bll.models.careerai.AiUseCase;
import com.trajectiv.bll.services.careerai.AiExecutionService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpportunityAnalysisServiceImpl implements OpportunityAnalysisService {

    private final AiExecutionService aiExecutionService;

    public OpportunityAnalysisServiceImpl(AiExecutionService aiExecutionService) {
        this.aiExecutionService = aiExecutionService;
    }

    @Override
    public OpportunityAnalysisBllDto analyze(AnalyzeOpportunityBllCommand command) {
        AiExecutionResultBllDto execution = aiExecutionService.execute(
                new AiExecutionCommandBllDto(
                        AiUseCase.OPPORTUNITY_EXTRACT,
                        Map.of("OPPORTUNITY_RAW_TEXT", command.description())
                )
        );

        return new OpportunityAnalysisBllDto(
                execution.executionId(),
                execution.schemaVersion(),
                execution.promptVersion(),
                execution.provider(),
                execution.model(),
                execution.durationMillis(),
                execution.artifact()
        );
    }
}