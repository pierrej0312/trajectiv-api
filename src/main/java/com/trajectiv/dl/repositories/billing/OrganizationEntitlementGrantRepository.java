package com.trajectiv.dl.repositories.billing;

import com.trajectiv.dl.entities.billing.OrganizationEntitlementGrant;
import com.trajectiv.dl.enums.entitlement.EntitlementSource;
import com.trajectiv.dl.enums.FeatureKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrganizationEntitlementGrantRepository
        extends JpaRepository<
        OrganizationEntitlementGrant,
        UUID
        > {

    List<OrganizationEntitlementGrant>
    findAllByOrganizationId(
            UUID organizationId
    );

    List<OrganizationEntitlementGrant>
    findAllByOrganizationIdAndFeatureKey(
            UUID organizationId,
            FeatureKey featureKey
    );

    List<OrganizationEntitlementGrant>
    findAllByOrganizationIdAndSource(
            UUID organizationId,
            EntitlementSource source
    );

    @Query("""
        select grant
        from OrganizationEntitlementGrant grant
        where grant.organization.id = :organizationId
          and (
                grant.validFrom is null
                or grant.validFrom <= :at
          )
          and (
                grant.validUntil is null
                or grant.validUntil > :at
          )
        order by grant.featureKey asc
        """)
    List<OrganizationEntitlementGrant>
    findAllActiveByOrganizationId(
            UUID organizationId,
            Instant at
    );

    @Query("""
        select grant
        from OrganizationEntitlementGrant grant
        where grant.organization.id = :organizationId
          and grant.featureKey = :featureKey
          and (
                grant.validFrom is null
                or grant.validFrom <= :at
          )
          and (
                grant.validUntil is null
                or grant.validUntil > :at
          )
        """)
    List<OrganizationEntitlementGrant>
    findAllActiveByOrganizationIdAndFeatureKey(
            UUID organizationId,
            FeatureKey featureKey,
            Instant at
    );

    void deleteAllByOrganizationIdAndSource(
            UUID organizationId,
            EntitlementSource source
    );
}