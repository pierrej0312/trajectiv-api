package com.trajectiv.dl.repositories.organization;

import com.trajectiv.dl.entities.organization.OrganizationMember;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import com.trajectiv.dl.enums.organization.OrganizationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationMemberRepository
        extends JpaRepository<OrganizationMember, UUID> {



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

    @Query("""
    select member
    from OrganizationMember member
    join fetch member.user user
    where member.organization.id = :organizationId
      and member.status in :statuses
    order by lower(user.displayName) asc
    """)
    List<OrganizationMember> findAllVisibleByOrganizationId(
            UUID organizationId,
            Collection<OrganizationMemberStatus> statuses
    );

    @Query("""
    select count(member)
    from OrganizationMember member
    where member.organization.id = :organizationId
      and member.role = :role
      and member.status = :status
    """)
    long countByOrganizationIdAndRoleAndStatus(
            UUID organizationId,
            OrganizationRole role,
            OrganizationMemberStatus status
    );

    @EntityGraph(attributePaths = {
            "user"
    })
    List<OrganizationMember>
    findAllByOrganization_IdAndStatusInOrderByJoinedAtAsc(
            UUID organizationId,
            Collection<OrganizationMemberStatus> statuses
    );

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "user",
            "organization"
    })
    @Query("""
    select member
    from OrganizationMember member
    where member.id = :memberId
      and member.organization.id = :organizationId
    """)
    Optional<OrganizationMember>
    findByIdAndOrganizationIdForUpdate(
            @Param("memberId")
            UUID memberId,

            @Param("organizationId")
            UUID organizationId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select member
    from OrganizationMember member
    where member.organization.id = :organizationId
      and member.role = :role
      and member.status = :status
    order by member.id
    """)
    List<OrganizationMember>
    findAllByOrganizationIdAndRoleAndStatusForUpdate(
            @Param("organizationId")
            UUID organizationId,

            @Param("role")
            OrganizationRole role,

            @Param("status")
            OrganizationMemberStatus status
    );
}