package com.trajectiv.bll.services.organization.member.policy;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.InvalidOrganizationMemberRoleChangeException;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class OrganizationMemberRolePolicy {

    public void validateChange(
            OrganizationMember actor,
            OrganizationMember target,
            OrganizationRole requestedRole,
            List<OrganizationMember> activeOwners
    ) {
        Objects.requireNonNull(
                actor,
                "actor cannot be null."
        );

        Objects.requireNonNull(
                target,
                "target cannot be null."
        );

        Objects.requireNonNull(
                requestedRole,
                "requestedRole cannot be null."
        );

        activeOwners = activeOwners == null
                ? List.of()
                : List.copyOf(activeOwners);

        requireTargetActive(target);

        if (target.getRole() == requestedRole) {
            return;
        }

        requireOwnerForOwnerAssignment(
                actor,
                requestedRole
        );

        protectOwnerFromAdmin(
                actor,
                target
        );

        protectLastOwner(
                target,
                requestedRole,
                activeOwners
        );
    }

    private void requireTargetActive(
            OrganizationMember target
    ) {
        if (!target.isActive()) {
            throw new InvalidOrganizationMemberRoleChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_NOT_ACTIVE,
                    "Only an active organization member can change role."
            );
        }
    }

    private void requireOwnerForOwnerAssignment(
            OrganizationMember actor,
            OrganizationRole requestedRole
    ) {
        if (
                requestedRole ==
                        OrganizationRole.ORGANIZATION_OWNER
                        &&
                        actor.getRole() !=
                                OrganizationRole.ORGANIZATION_OWNER
        ) {
            throw new InvalidOrganizationMemberRoleChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_OWNER_ASSIGNMENT_FORBIDDEN,
                    "Only an organization owner can assign the owner role."
            );
        }
    }

    private void protectOwnerFromAdmin(
            OrganizationMember actor,
            OrganizationMember target
    ) {
        if (
                actor.getRole() ==
                        OrganizationRole.ORGANIZATION_ADMIN
                        &&
                        target.getRole() ==
                                OrganizationRole.ORGANIZATION_OWNER
        ) {
            throw new InvalidOrganizationMemberRoleChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_OWNER_PROTECTED,
                    "An organization administrator cannot modify an owner."
            );
        }
    }

    private void protectLastOwner(
            OrganizationMember target,
            OrganizationRole requestedRole,
            List<OrganizationMember> activeOwners
    ) {
        boolean removesOwnerRole =
                target.getRole()
                        == OrganizationRole.ORGANIZATION_OWNER
                        && requestedRole
                        != OrganizationRole.ORGANIZATION_OWNER;

        if (
                removesOwnerRole
                        && activeOwners.size() <= 1
        ) {
            throw new InvalidOrganizationMemberRoleChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_LAST_OWNER,
                    "The last active organization owner cannot be demoted."
            );
        }
    }
}