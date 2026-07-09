package com.trajectiv.dl.repositories;

import com.trajectiv.dl.entities.JobRole;
import com.trajectiv.dl.enums.JobRoleFamily;
import com.trajectiv.dl.projections.JobRoleSuggestionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRoleRepository extends JpaRepository<JobRole, UUID> {

    Optional<JobRole> findByIdAndActiveTrue(UUID id);

    Optional<JobRole> findBySlugAndActiveTrue(String slug);

    List<JobRole> findByActiveTrueOrderBySortOrderAscLabelAsc();

    List<JobRole> findByActiveTrueAndFamilyOrderBySortOrderAscLabelAsc(JobRoleFamily family);

    @Query(
            value = """
                    SELECT
                        jr.id AS id,
                        jr.slug AS slug,
                        jr.label AS label,
                        jr.description AS description,
                        jr.family AS family,
                        jr.tags_json::text AS tagsJson,
                        jr.aliases_json::text AS aliasesJson,
                        jr.sort_order AS sortOrder,
                        CASE
                            WHEN :query IS NULL OR btrim(:query) = '' THEN
                                1000 - jr.sort_order
                            WHEN lower(jr.label) = lower(:query) THEN
                                10000
                            WHEN lower(jr.slug) = lower(concat(:query, '-developer')) THEN
                                9000
                            WHEN lower(jr.label) LIKE lower(concat(:query, '%')) THEN
                                8000
                            WHEN lower(jr.label) LIKE lower(concat('%', :query, '%')) THEN
                                6000
                            WHEN lower(jr.slug) LIKE lower(concat('%', :query, '%')) THEN
                                5000
                            WHEN EXISTS (
                                SELECT 1
                                FROM jsonb_array_elements_text(jr.aliases_json) alias
                                WHERE lower(alias) LIKE lower(concat('%', :query, '%'))
                            ) THEN
                                4500
                            WHEN EXISTS (
                                SELECT 1
                                FROM jsonb_array_elements_text(jr.tags_json) tag
                                WHERE lower(tag) LIKE lower(concat('%', :query, '%'))
                            ) THEN
                                3500
                            ELSE
                                0
                        END AS score
                    FROM job_roles jr
                    WHERE jr.active = true
                      AND (:family IS NULL OR jr.family = :family)
                      AND (
                            :query IS NULL
                            OR btrim(:query) = ''
                            OR lower(jr.label) LIKE lower(concat('%', :query, '%'))
                            OR lower(jr.slug) LIKE lower(concat('%', :query, '%'))
                            OR EXISTS (
                                SELECT 1
                                FROM jsonb_array_elements_text(jr.aliases_json) alias
                                WHERE lower(alias) LIKE lower(concat('%', :query, '%'))
                            )
                            OR EXISTS (
                                SELECT 1
                                FROM jsonb_array_elements_text(jr.tags_json) tag
                                WHERE lower(tag) LIKE lower(concat('%', :query, '%'))
                            )
                      )
                    ORDER BY score DESC, jr.sort_order ASC, jr.label ASC
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<JobRoleSuggestionProjection> searchSuggestions(
            @Param("query") String query,
            @Param("family") String family,
            @Param("limit") int limit
    );
}