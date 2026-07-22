package com.trajectiv.bll.services.organization.member.policy;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.InvalidOrganizationMemberStatusChangeException;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class OrganizationMemberLifecyclePolicy {

    public void validateSuspension(
            OrganizationMember actor,
            OrganizationMember target,
            List<OrganizationMember> activeOwners
    ) {
        requireMembers(actor, target);
        forbidSelfStatusChange(actor, target);
        protectOwnerFromAdmin(actor, target);
        requireStatus(
                target,
                OrganizationMemberStatus.ACTIVE,
                BusinessErrorCode
                        .ORGANIZATION_MEMBER_ALREADY_SUSPENDED,
                "Only an active organization member can be suspended."
        );
        protectLastActiveOwner(
                target,
                activeOwners
        );
    }

    public void validateReactivation(
            OrganizationMember actor,
            OrganizationMember target
    ) {
        requireMembers(actor, target);
        forbidSelfStatusChange(actor, target);
        protectOwnerFromAdmin(actor, target);
        requireStatus(
                target,
                OrganizationMemberStatus.SUSPENDED,
                BusinessErrorCode
                        .ORGANIZATION_MEMBER_NOT_SUSPENDED,
                "Only a suspended organization member can be reactivated."
        );
    }

    public void validateRemoval(
            OrganizationMember actor,
            OrganizationMember target,
            List<OrganizationMember> activeOwners
    ) {
        requireMembers(actor, target);
        forbidSelfRemoval(actor, target);
        protectOwnerFromAdmin(actor, target);

        if (
                target.getStatus() ==
                        OrganizationMemberStatus.REMOVED
        ) {
            throw new InvalidOrganizationMemberStatusChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_ALREADY_REMOVED,
                    "Organization member is already removed."
            );
        }

        protectLastActiveOwner(
                target,
                activeOwners
        );
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
            throw new InvalidOrganizationMemberStatusChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_OWNER_PROTECTED,
                    "An organization administrator cannot modify an owner."
            );
        }
    }

    private void protectLastActiveOwner(
            OrganizationMember target,
            List<OrganizationMember> activeOwners
    ) {
        boolean removesActiveOwner =
                target.getRole() ==
                        OrganizationRole.ORGANIZATION_OWNER
                        &&
                        target.getStatus() ==
                                OrganizationMemberStatus.ACTIVE;

        if (
                removesActiveOwner
                        && activeOwners.size() <= 1
        ) {
            throw new InvalidOrganizationMemberStatusChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_LAST_OWNER,
                    "The last active organization owner cannot be suspended or removed."
            );
        }
    }

    private void forbidSelfStatusChange(
            OrganizationMember actor,
            OrganizationMember target
    ) {
        if (actor.getId().equals(target.getId())) {
            throw new InvalidOrganizationMemberStatusChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_SELF_STATUS_CHANGE_FORBIDDEN,
                    "A member cannot suspend or reactivate their own membership."
            );
        }
    }

    private void forbidSelfRemoval(
            OrganizationMember actor,
            OrganizationMember target
    ) {
        if (actor.getId().equals(target.getId())) {
            throw new InvalidOrganizationMemberStatusChangeException(
                    BusinessErrorCode
                            .ORGANIZATION_MEMBER_SELF_REMOVAL_FORBIDDEN,
                    "A member cannot remove their own membership through member management."
            );
        }
    }

    private void requireStatus(
            OrganizationMember target,
            OrganizationMemberStatus requiredStatus,
            BusinessErrorCode errorCode,
            String message
    ) {
        if (target.getStatus() != requiredStatus) {
            throw new InvalidOrganizationMemberStatusChangeException(
                    errorCode,
                    message
            );
        }
    }

    private void requireMembers(
            OrganizationMember actor,
            OrganizationMember target
    ) {
        Objects.requireNonNull(
                actor,
                "actor cannot be null."
        );

        Objects.requireNonNull(
                target,
                "target cannot be null."
        );
    }
}