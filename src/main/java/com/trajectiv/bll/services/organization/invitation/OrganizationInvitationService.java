package com.trajectiv.bll.services.organization.invitation;

import com.trajectiv.bll.dto.organization.invitation.AcceptOrganizationInvitationBllCommand;
import com.trajectiv.bll.dto.organization.invitation.CreateOrganizationInvitationBllCommand;
import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationAcceptanceBllDto;
import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationBllDto;

import java.util.List;
import java.util.UUID;

public interface OrganizationInvitationService {

    OrganizationInvitationBllDto invite(
            UUID currentUserId,
            UUID organizationId,
            CreateOrganizationInvitationBllCommand command
    );

    List<OrganizationInvitationBllDto> getInvitations(
            UUID currentUserId,
            UUID organizationId
    );

    OrganizationInvitationAcceptanceBllDto accept(
            UUID currentUserId,
            AcceptOrganizationInvitationBllCommand command
    );

    void revoke(
            UUID currentUserId,
            UUID organizationId,
            UUID invitationId
    );

    OrganizationInvitationBllDto resend(
            UUID currentUserId,
            UUID organizationId,
            UUID invitationId
    );
}