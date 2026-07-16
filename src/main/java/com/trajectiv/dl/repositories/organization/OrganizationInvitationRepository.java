package com.trajectiv.dl.repositories.organization;

import com.trajectiv.dl.entities.organization.OrganizationInvitation;
import com.trajectiv.dl.enums.organization.OrganizationInvitationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationInvitationRepository
        extends JpaRepository<
        OrganizationInvitation,
        UUID
        > {

    boolean existsByOrganizationIdAndEmailIgnoreCaseAndStatus(
            UUID organizationId,
            String email,
            OrganizationInvitationStatus status
    );

    @Query("""
        select invitation
        from OrganizationInvitation invitation
        join fetch invitation.organization organization
        join fetch invitation.invitedByUser invitedBy
        where organization.id = :organizationId
        order by invitation.createdAt desc
        """)
    List<OrganizationInvitation>
    findAllByOrganizationIdWithRelations(
            UUID organizationId
    );

    @Query("""
        select invitation
        from OrganizationInvitation invitation
        join fetch invitation.organization
        join fetch invitation.invitedByUser
        where invitation.id = :invitationId
          and invitation.organization.id = :organizationId
        """)
    Optional<OrganizationInvitation>
    findByIdAndOrganizationIdWithRelations(
            UUID invitationId,
            UUID organizationId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select invitation
        from OrganizationInvitation invitation
        join fetch invitation.organization
        where invitation.tokenHash = :tokenHash
        """)
    Optional<OrganizationInvitation>
    findByTokenHashForUpdate(
            String tokenHash
    );
}