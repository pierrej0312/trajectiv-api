package com.trajectiv.bll.services.jobrole;

import com.trajectiv.bll.dto.jobrole.JobRoleFamilyBll;
import com.trajectiv.bll.dto.jobrole.JobRoleSuggestionBllDto;

import java.util.List;

public interface JobRoleCatalogService {

    List<JobRoleSuggestionBllDto> searchSuggestions(
            String query,
            JobRoleFamilyBll family,
            Integer limit
    );
}