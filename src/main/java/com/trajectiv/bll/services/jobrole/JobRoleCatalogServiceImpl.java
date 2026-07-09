package com.trajectiv.bll.services.jobrole;

import com.trajectiv.bll.dto.jobrole.JobRoleFamilyBll;
import com.trajectiv.bll.dto.jobrole.JobRoleSuggestionBllDto;
import com.trajectiv.bll.mappers.JobRoleBllMapper;
import com.trajectiv.dl.repositories.JobRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobRoleCatalogServiceImpl implements JobRoleCatalogService {

    private static final int DEFAULT_LIMIT = 12;
    private static final int MAX_LIMIT = 30;

    private final JobRoleRepository jobRoleRepository;
    private final JobRoleBllMapper jobRoleBllMapper;

    public JobRoleCatalogServiceImpl(
            JobRoleRepository jobRoleRepository,
            JobRoleBllMapper jobRoleBllMapper
    ) {
        this.jobRoleRepository = jobRoleRepository;
        this.jobRoleBllMapper = jobRoleBllMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobRoleSuggestionBllDto> searchSuggestions(
            String query,
            JobRoleFamilyBll family,
            Integer limit
    ) {
        int safeLimit = resolveLimit(limit);
        String safeQuery = normalizeQuery(query);
        String familyValue = family != null ? family.name() : null;

        return jobRoleRepository.searchSuggestions(
                        safeQuery,
                        familyValue,
                        safeLimit
                )
                .stream()
                .map(jobRoleBllMapper::toDto)
                .toList();
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }

        return Math.min(limit, MAX_LIMIT);
    }

    private String normalizeQuery(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }

        return query.trim();
    }
}