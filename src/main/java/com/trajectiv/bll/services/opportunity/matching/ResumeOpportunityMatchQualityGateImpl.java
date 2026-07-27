package com.trajectiv.bll.services.opportunity.matching;

import com.fasterxml.jackson.databind.JsonNode;
import com.trajectiv.bll.exceptions.opportunity.InvalidResumeOpportunityMatchException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ResumeOpportunityMatchQualityGateImpl
        implements ResumeOpportunityMatchQualityGate {

    @Override
    public void validate(JsonNode opportunityArtifact, JsonNode matchArtifact) {
        Map<String, String> priorityByRequirementId = readOpportunityRequirements(
                opportunityArtifact.path("requirements")
        );
        Map<String, String> statusByRequirementId = readMatches(
                matchArtifact.path("requirementMatches")
        );

        if (!priorityByRequirementId.keySet().equals(statusByRequirementId.keySet())) {
            Set<String> missingMatches = new HashSet<>(priorityByRequirementId.keySet());
            missingMatches.removeAll(statusByRequirementId.keySet());

            Set<String> unknownMatches = new HashSet<>(statusByRequirementId.keySet());
            unknownMatches.removeAll(priorityByRequirementId.keySet());

            throw new InvalidResumeOpportunityMatchException(
                    "Requirement references are inconsistent. Missing matches=%s, unknown matches=%s"
                            .formatted(missingMatches, unknownMatches)
            );
        }

        validateCoverageCounts(
                matchArtifact.path("requirementCoverage"),
                priorityByRequirementId,
                statusByRequirementId
        );
    }

    private Map<String, String> readOpportunityRequirements(JsonNode requirements) {
        if (!requirements.isArray()) {
            throw new InvalidResumeOpportunityMatchException(
                    "Opportunity artifact does not contain a requirements array"
            );
        }

        Map<String, String> result = new HashMap<>();
        for (JsonNode requirement : requirements) {
            String id = requiredText(requirement, "id");
            String priority = requiredText(requirement, "priority");
            if (result.putIfAbsent(id, priority) != null) {
                throw new InvalidResumeOpportunityMatchException(
                        "Duplicate opportunity requirement id: " + id
                );
            }
        }
        return result;
    }

    private Map<String, String> readMatches(JsonNode matches) {
        if (!matches.isArray()) {
            throw new InvalidResumeOpportunityMatchException(
                    "Match artifact does not contain a requirementMatches array"
            );
        }

        Map<String, String> result = new HashMap<>();
        for (JsonNode match : matches) {
            String id = requiredText(match, "requirementId");
            String status = requiredText(match, "status");
            if (result.putIfAbsent(id, status) != null) {
                throw new InvalidResumeOpportunityMatchException(
                        "Duplicate match requirement id: " + id
                );
            }
        }
        return result;
    }

    private void validateCoverageCounts(
            JsonNode coverage,
            Map<String, String> priorities,
            Map<String, String> statuses
    ) {
        if (!coverage.isObject()) {
            throw new InvalidResumeOpportunityMatchException(
                    "Match artifact does not contain requirementCoverage"
            );
        }

        assertCount(coverage, "totalRequirements", priorities.size());

        Map<String, Integer> globalCounts = countStatuses(statuses, null, priorities);
        assertCount(coverage, "matched", globalCounts.get("MATCHED"));
        assertCount(coverage, "partial", globalCounts.get("PARTIAL"));
        assertCount(coverage, "missing", globalCounts.get("MISSING"));
        assertCount(coverage, "unknown", globalCounts.get("UNKNOWN"));
        assertCount(
                coverage,
                "evaluableRequirements",
                priorities.size() - globalCounts.get("UNKNOWN")
        );

        validatePriorityCountSet(coverage.path("mandatory"), "MANDATORY", priorities, statuses);
        validatePriorityCountSet(coverage.path("preferred"), "PREFERRED", priorities, statuses);
        validatePriorityCountSet(coverage.path("contextual"), "CONTEXTUAL", priorities, statuses);
    }

    private void validatePriorityCountSet(
            JsonNode countSet,
            String priority,
            Map<String, String> priorities,
            Map<String, String> statuses
    ) {
        Map<String, Integer> counts = countStatuses(statuses, priority, priorities);
        assertCount(countSet, "matched", counts.get("MATCHED"));
        assertCount(countSet, "partial", counts.get("PARTIAL"));
        assertCount(countSet, "missing", counts.get("MISSING"));
        assertCount(countSet, "unknown", counts.get("UNKNOWN"));
    }

    private Map<String, Integer> countStatuses(
            Map<String, String> statuses,
            String expectedPriority,
            Map<String, String> priorities
    ) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("MATCHED", 0);
        counts.put("PARTIAL", 0);
        counts.put("MISSING", 0);
        counts.put("UNKNOWN", 0);

        for (Map.Entry<String, String> entry : statuses.entrySet()) {
            if (expectedPriority != null
                    && !expectedPriority.equals(priorities.get(entry.getKey()))) {
                continue;
            }
            counts.computeIfPresent(entry.getValue(), (ignored, count) -> count + 1);
        }
        return counts;
    }

    private void assertCount(JsonNode node, String field, int expected) {
        int actual = node.path(field).asInt(Integer.MIN_VALUE);
        if (actual != expected) {
            throw new InvalidResumeOpportunityMatchException(
                    "Invalid coverage count for %s: expected=%d, actual=%d"
                            .formatted(field, expected, actual)
            );
        }
    }

    private String requiredText(JsonNode node, String field) {
        String value = node.path(field).asText(null);
        if (value == null || value.isBlank()) {
            throw new InvalidResumeOpportunityMatchException(
                    "Required field is missing: " + field
            );
        }
        return value;
    }
}