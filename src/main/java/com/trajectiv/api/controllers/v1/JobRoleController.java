package com.trajectiv.api.controllers.v1;

import com.trajectiv.api.dto.jobRole.JobRoleFamilyApiDto;
import com.trajectiv.api.dto.jobRole.JobRoleSuggestionApiDto;
import com.trajectiv.api.mappers.JobRoleApiMapper;
import com.trajectiv.api.routes.ApiRoutes;
import com.trajectiv.bll.services.jobrole.JobRoleCatalogService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = ApiRoutes.V1.JOB_ROLES,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class JobRoleController {

    private final JobRoleCatalogService jobRoleCatalogService;
    private final JobRoleApiMapper jobRoleApiMapper;

    public JobRoleController(
            JobRoleCatalogService jobRoleCatalogService,
            JobRoleApiMapper jobRoleApiMapper
    ) {
        this.jobRoleCatalogService = jobRoleCatalogService;
        this.jobRoleApiMapper = jobRoleApiMapper;
    }

    @GetMapping
    public List<JobRoleSuggestionApiDto> searchJobRoles(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) JobRoleFamilyApiDto family,
            @RequestParam(required = false) Integer limit
    ) {
        return jobRoleCatalogService.searchSuggestions(
                        query,
                        jobRoleApiMapper.toBllFamily(family),
                        limit
                )
                .stream()
                .map(jobRoleApiMapper::toSuggestionApiDto)
                .toList();
    }
}