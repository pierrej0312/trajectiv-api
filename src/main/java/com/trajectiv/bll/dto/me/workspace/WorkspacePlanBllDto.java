package com.trajectiv.bll.dto.me.workspace;

import com.trajectiv.dl.enums.billing.SubscriptionStatus;

import java.util.Objects;

public record WorkspacePlanBllDto(
        String code,
        String label,
        SubscriptionStatus status
) {

    public WorkspacePlanBllDto {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Workspace plan code cannot be blank."
            );
        }

        code = code.trim().toUpperCase();

        label = label == null || label.isBlank()
                ? code
                : label.trim();

        Objects.requireNonNull(
                status,
                "Workspace plan status cannot be null."
        );
    }
}