package com.trajectiv.dl.repositories.organization;

import com.trajectiv.dl.entities.organization.Organization;
import com.trajectiv.dl.enums.organization.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository
        extends JpaRepository<Organization, UUID> {

    Optional<Organization> findBySlug(
            String slug
    );

    Optional<Organization> findByIdAndStatus(
            UUID id,
            OrganizationStatus status
    );

    List<Organization> findAllByStatusOrderByNameAsc(
            OrganizationStatus status
    );

    boolean existsBySlug(
            String slug
    );
}