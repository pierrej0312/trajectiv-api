package com.trajectiv.bll.services.organization;

import com.trajectiv.bll.dto.organization.CreateOrganizationBllCommand;
import com.trajectiv.bll.dto.organization.OrganizationBllDto;
import com.trajectiv.bll.dto.organization.UpdateOrganizationBllCommand;

import java.util.UUID;

public interface OrganizationService {

    OrganizationBllDto createForCurrentUser(
            UUID currentUserId,
            CreateOrganizationBllCommand command
    );

    OrganizationBllDto getAccessibleOrganization(
            UUID currentUserId,
            UUID organizationId
    );

    OrganizationBllDto updateOrganization(
            UUID currentUserId,
            UUID organizationId,
            UpdateOrganizationBllCommand command
    );

    void archiveOrganization(
            UUID currentUserId,
            UUID organizationId
    );
}