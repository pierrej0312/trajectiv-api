package com.trajectiv.bll.mappers;

import com.trajectiv.bll.dto.jobrole.JobRoleFamilyBll;
import com.trajectiv.bll.dto.jobrole.JobRoleSuggestionBllDto;
import com.trajectiv.bll.utils.JsonStringListParser;
import com.trajectiv.dl.projections.JobRoleSuggestionProjection;
import org.springframework.stereotype.Component;

@Component
public class JobRoleBllMapper {

    private final JsonStringListParser jsonStringListParser;

    public JobRoleBllMapper(JsonStringListParser jsonStringListParser) {
        this.jsonStringListParser = jsonStringListParser;
    }

    public JobRoleSuggestionBllDto toDto(JobRoleSuggestionProjection projection) {
        return new JobRoleSuggestionBllDto(
                projection.getId(),
                projection.getSlug(),
                projection.getLabel(),
                projection.getDescription(),
                toFamily(projection.getFamily()),
                jsonStringListParser.parse(projection.getTagsJson()),
                jsonStringListParser.parse(projection.getAliasesJson()),
                projection.getSortOrder() != null ? projection.getSortOrder() : 0,
                projection.getScore() != null ? projection.getScore() : 0
        );
    }

    private JobRoleFamilyBll toFamily(String family) {
        if (family == null || family.isBlank()) {
            return JobRoleFamilyBll.OTHER;
        }

        try {
            return JobRoleFamilyBll.valueOf(family);
        } catch (IllegalArgumentException exception) {
            return JobRoleFamilyBll.OTHER;
        }
    }
}