package com.trajectiv.bll.exceptions;

import java.util.UUID;

public class OrganizationMemberNotFoundException
        extends RuntimeException {

    private final UUID memberId;
    private final UUID organizationId;

    public OrganizationMemberNotFoundException(
            UUID memberId,
            UUID organizationId
    ) {
        super(
                "Organization member %s was not found in organization %s."
                        .formatted(
                                memberId,
                                organizationId
                        )
        );

        this.memberId = memberId;
        this.organizationId = organizationId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }
}