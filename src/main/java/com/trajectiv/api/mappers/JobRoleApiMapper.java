package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.jobRole.JobRoleFamilyApiDto;
import com.trajectiv.api.dto.jobRole.JobRoleSuggestionApiDto;
import com.trajectiv.bll.dto.jobrole.JobRoleFamilyBll;
import com.trajectiv.bll.dto.jobrole.JobRoleSuggestionBllDto;
import org.springframework.stereotype.Component;

@Component
public class JobRoleApiMapper {

    public JobRoleSuggestionApiDto toSuggestionApiDto(JobRoleSuggestionBllDto dto) {
        return new JobRoleSuggestionApiDto(
                dto.id(),
                dto.slug(),
                dto.label(),
                dto.description(),
                toApiFamily(dto.family()),
                dto.tags(),
                dto.aliases(),
                dto.sortOrder(),
                dto.score()
        );
    }

    public JobRoleFamilyBll toBllFamily(JobRoleFamilyApiDto family) {
        if (family == null) {
            return null;
        }

        return JobRoleFamilyBll.valueOf(family.name());
    }

    private JobRoleFamilyApiDto toApiFamily(JobRoleFamilyBll family) {
        if (family == null) {
            return JobRoleFamilyApiDto.OTHER;
        }

        return JobRoleFamilyApiDto.valueOf(family.name());
    }
}