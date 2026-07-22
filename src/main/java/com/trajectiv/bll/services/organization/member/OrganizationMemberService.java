package com.trajectiv.bll.services.organization.member;

import com.trajectiv.bll.dto.organization.member.OrganizationMemberBllDto;
import com.trajectiv.bll.dto.organization.member.UpdateOrganizationMemberRoleBllCommand;

import java.util.List;
import java.util.UUID;

public interface OrganizationMemberService {

    List<OrganizationMemberBllDto> getMembers(
            UUID currentUserId,
            UUID organizationId
    );

    OrganizationMemberBllDto changeRole(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId,
            UpdateOrganizationMemberRoleBllCommand command
    );

    OrganizationMemberBllDto suspend(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId
    );

    OrganizationMemberBllDto reactivate(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId
    );

    void remove(
            UUID currentUserId,
            UUID organizationId,
            UUID memberId
    );
}