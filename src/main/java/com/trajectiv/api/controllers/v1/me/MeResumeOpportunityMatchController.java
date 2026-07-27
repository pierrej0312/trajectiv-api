package com.trajectiv.api.controllers.v1.me;

import com.trajectiv.api.dto.me.opportunity.matching.MatchResumeOpportunityRequestApiDto;
import com.trajectiv.api.dto.me.opportunity.matching.ResumeOpportunityMatchResponseApiDto;
import com.trajectiv.api.mappers.ResumeOpportunityMatchApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.opportunity.matching.ResumeOpportunityMatchBllDto;
import com.trajectiv.bll.services.opportunity.matching.ResumeOpportunityMatchService;
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
        value = ApiRoutes.V1.ME_RESUME_OPPORTUNITY_MATCHES,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class MeResumeOpportunityMatchController {

    private final ResumeOpportunityMatchService matchService;
    private final ResumeOpportunityMatchApiMapper matchApiMapper;

    public MeResumeOpportunityMatchController(
            ResumeOpportunityMatchService matchService,
            ResumeOpportunityMatchApiMapper matchApiMapper
    ) {
        this.matchService = matchService;
        this.matchApiMapper = matchApiMapper;
    }

    @PostMapping
    public ResponseEntity<ResumeOpportunityMatchResponseApiDto> match(
            @Valid @RequestBody MatchResumeOpportunityRequestApiDto request
    ) {
        ResumeOpportunityMatchBllDto result = matchService.match(
                matchApiMapper.toBllCommand(request)
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matchApiMapper.toApiDto(result));
    }
}
