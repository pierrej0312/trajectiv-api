package com.trajectiv.bll.mappers.organization;

import com.trajectiv.bll.dto.organization.invitation.OrganizationInvitationBllDto;
import com.trajectiv.dl.entities.organization.OrganizationInvitation;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrganizationInvitationBllMapper {

    public OrganizationInvitationBllDto toDto(
            OrganizationInvitation invitation
    ) {
        Objects.requireNonNull(
                invitation,
                "invitation cannot be null."
        );

        return new OrganizationInvitationBllDto(
                invitation.getId(),
                invitation.getOrganization().getId(),
                invitation.getOrganization().getName(),
                invitation.getEmail(),
                invitation.getRole(),
                invitation.getStatus(),
                invitation.getInvitedByUser().getId(),
                invitation.getInvitedByUser().getDisplayName(),
                invitation.getExpiresAt(),
                invitation.getAcceptedAt(),
                invitation.getRevokedAt(),
                invitation.getCreatedAt(),
                invitation.getUpdatedAt()
        );
    }
}