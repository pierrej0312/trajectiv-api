package com.trajectiv.bll.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrganizationMemberNotFoundException
        extends BusinessException {

    private final UUID memberId;
    private final UUID organizationId;

    public OrganizationMemberNotFoundException(
            UUID memberId,
            UUID organizationId
    ) {
        super(
                BusinessErrorCode.ORGANIZATION_MEMBER_NOT_FOUND,
                "Organization member %s was not found in organization %s."
                        .formatted(memberId, organizationId)
        );
        this.memberId = memberId;
        this.organizationId = organizationId;
    }

}