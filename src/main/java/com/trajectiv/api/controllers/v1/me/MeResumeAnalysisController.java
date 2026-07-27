package com.trajectiv.api.controllers.v1.me;

import com.trajectiv.api.dto.me.resume.analysis.ResumeAnalysisResponseApiDto;
import com.trajectiv.api.mappers.ResumeAnalysisApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.dto.resume.analysis.ResumeAnalysisBllDto;
import com.trajectiv.bll.services.resume.analysis.ResumeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.ME_RESUME_ANALYSES,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@SecurityRequirement(
        name = "keycloakOAuth2",
        scopes = {"openid", "profile", "email"}
)
public class MeResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;
    private final ResumeAnalysisApiMapper resumeAnalysisApiMapper;

    public MeResumeAnalysisController(
            ResumeAnalysisService resumeAnalysisService,
            ResumeAnalysisApiMapper resumeAnalysisApiMapper
    ) {
        this.resumeAnalysisService = resumeAnalysisService;
        this.resumeAnalysisApiMapper = resumeAnalysisApiMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Analyze a PDF resume for the current user")
    public ResponseEntity<ResumeAnalysisResponseApiDto> analyze(
            @Parameter(
                    description = "Text-based PDF resume, maximum 10 MB.",
                    required = true
            )
            @RequestPart("file") MultipartFile file
    ) {
        ResumeAnalysisBllDto result = resumeAnalysisService.analyze(
                resumeAnalysisApiMapper.toBllCommand(file)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resumeAnalysisApiMapper.toApiDto(result));
    }
}