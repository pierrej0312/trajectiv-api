package com.trajectiv.bll.services.opportunity.matching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trajectiv.bll.dto.careerai.AiExecutionCommandBllDto;
import com.trajectiv.bll.dto.careerai.AiExecutionResultBllDto;
import com.trajectiv.bll.dto.opportunity.matching.MatchResumeOpportunityBllCommand;
import com.trajectiv.bll.dto.opportunity.matching.ResumeOpportunityMatchBllDto;
import com.trajectiv.bll.exceptions.careerai.AiPromptRenderingException;
import com.trajectiv.bll.models.careerai.AiUseCase;
import com.trajectiv.bll.services.careerai.AiExecutionService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ResumeOpportunityMatchServiceImpl
        implements ResumeOpportunityMatchService {

    private final AiExecutionService aiExecutionService;
    private final ResumeOpportunityMatchQualityGate qualityGate;
    private final ObjectMapper objectMapper;

    public ResumeOpportunityMatchServiceImpl(
            AiExecutionService aiExecutionService,
            ResumeOpportunityMatchQualityGate qualityGate,
            ObjectMapper objectMapper
    ) {
        this.aiExecutionService = aiExecutionService;
        this.qualityGate = qualityGate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResumeOpportunityMatchBllDto match(
            MatchResumeOpportunityBllCommand command
    ) {
        AiExecutionResultBllDto execution =
                aiExecutionService.execute(
                        new AiExecutionCommandBllDto(
                                AiUseCase.RESUME_OPPORTUNITY_MATCH,
                                Map.of(
                                        "RESUME_ANALYSIS_JSON",
                                        writeJson(
                                                command.resumeAnalysis()
                                        ),
                                        "OPPORTUNITY_ANALYSIS_JSON",
                                        writeJson(
                                                command.opportunityAnalysis()
                                        )
                                )
                        )
                );

        qualityGate.validate(
                command.opportunityAnalysis(),
                execution.artifact()
        );

        return new ResumeOpportunityMatchBllDto(
                execution.executionId(),
                execution.schemaVersion(),
                execution.promptVersion(),
                execution.provider(),
                execution.model(),
                execution.durationMillis(),
                execution.artifact()
        );
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new AiPromptRenderingException(
                    "Unable to serialize analysis artifacts for matching.",
                    exception
            );
        }
    }
}