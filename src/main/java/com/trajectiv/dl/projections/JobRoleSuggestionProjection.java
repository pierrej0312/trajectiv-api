package com.trajectiv.dl.projections;

import java.util.UUID;

public interface JobRoleSuggestionProjection {

    UUID getId();

    String getSlug();

    String getLabel();

    String getDescription();

    String getFamily();

    String getTagsJson();

    String getAliasesJson();

    Integer getSortOrder();

    Integer getScore();
}