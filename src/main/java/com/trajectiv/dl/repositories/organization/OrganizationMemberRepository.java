package com.trajectiv.dl.repositories.organization;

import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import com.trajectiv.dl.enums.organization.OrganizationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationMemberRepository
        extends JpaRepository<OrganizationMember, UUID> {

    @EntityGraph(attributePaths = {
            "organization"
    })
    List<OrganizationMember>
    findAllByUserIdAndStatusOrderByOrganizationNameAsc(
            UUID userId,
            OrganizationMemberStatus status
    );

    @EntityGraph(attributePaths = {
            "organization"
    })
    Optional<OrganizationMember>
    findByUserIdAndOrganizationId(
            UUID userId,
            UUID organizationId
    );

    List<OrganizationMember>
    findAllByOrganizationIdAndStatusOrderByJoinedAtAsc(
            UUID organizationId,
            OrganizationMemberStatus status
    );

    List<OrganizationMember>
    findAllByOrganizationIdAndRoleAndStatus(
            UUID organizationId,
            OrganizationRole role,
            OrganizationMemberStatus status
    );

    long countByOrganizationIdAndStatus(
            UUID organizationId,
            OrganizationMemberStatus status
    );

    boolean existsByUserIdAndOrganizationId(
            UUID userId,
            UUID organizationId
    );

    boolean existsByOrganizationIdAndRoleAndStatus(
            UUID organizationId,
            OrganizationRole role,
            OrganizationMemberStatus status
    );

    @Query("""
        select member
        from OrganizationMember member
        join fetch member.organization organization
        where member.user.id = :userId
          and member.status = :memberStatus
          and organization.status = :organizationStatus
        order by organization.name asc
        """)
    List<OrganizationMember> findAllByUserIdAndStatuses(
            UUID userId,
            OrganizationMemberStatus memberStatus,
            OrganizationStatus organizationStatus
    );

    @Query("""
        select member
        from OrganizationMember member
        join fetch member.organization organization
        where member.user.id = :userId
          and organization.id = :organizationId
          and member.status = :status
        """)
    Optional<OrganizationMember>
    findByUserIdAndOrganizationIdAndStatus(
            UUID userId,
            UUID organizationId,
            OrganizationMemberStatus status
    );

    @Query("""
        select case when count(member) > 0
            then true
            else false
        end
        from OrganizationMember member
        join member.user user
        where member.organization.id = :organizationId
          and lower(user.email) = lower(:email)
          and member.status in :statuses
        """)
    boolean existsByOrganizationIdAndEmailAndStatuses(
            UUID organizationId,
            String email,
            Collection<OrganizationMemberStatus> statuses
    );
}