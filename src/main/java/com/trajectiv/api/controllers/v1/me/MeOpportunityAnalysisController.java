package com.trajectiv.api.controllers.v1.me;

import com.trajectiv.api.dto.me.opportunity.analysis.AnalyzeOpportunityRequestApiDto;
import com.trajectiv.api.dto.me.opportunity.analysis.OpportunityAnalysisResponseApiDto;
import com.trajectiv.api.mappers.OpportunityAnalysisApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.opportunity.analysis.OpportunityAnalysisBllDto;
import com.trajectiv.bll.services.opportunity.analysis.OpportunityAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ME_OPPORTUNITY_ANALYSES,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class MeOpportunityAnalysisController {

    private final OpportunityAnalysisService opportunityAnalysisService;
    private final OpportunityAnalysisApiMapper opportunityAnalysisApiMapper;

    public MeOpportunityAnalysisController(
            OpportunityAnalysisService opportunityAnalysisService,
            OpportunityAnalysisApiMapper opportunityAnalysisApiMapper
    ) {
        this.opportunityAnalysisService = opportunityAnalysisService;
        this.opportunityAnalysisApiMapper = opportunityAnalysisApiMapper;
    }

    @PostMapping
    public ResponseEntity<OpportunityAnalysisResponseApiDto> analyze(
            @Valid @RequestBody AnalyzeOpportunityRequestApiDto request
    ) {
        OpportunityAnalysisBllDto result = opportunityAnalysisService.analyze(
                opportunityAnalysisApiMapper.toBllCommand(request)
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(opportunityAnalysisApiMapper.toApiDto(result));
    }
}