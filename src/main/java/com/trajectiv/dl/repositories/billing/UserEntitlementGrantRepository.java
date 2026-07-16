package com.trajectiv.dl.repositories.billing;

import com.trajectiv.dl.entities.billing.UserEntitlementGrant;
import com.trajectiv.dl.enums.entitlement.EntitlementSource;
import com.trajectiv.dl.enums.FeatureKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserEntitlementGrantRepository
        extends JpaRepository<
        UserEntitlementGrant,
        UUID
        > {

    List<UserEntitlementGrant>
    findAllByUserId(
            UUID userId
    );

    List<UserEntitlementGrant>
    findAllByUserIdAndFeatureKey(
            UUID userId,
            FeatureKey featureKey
    );

    List<UserEntitlementGrant>
    findAllByUserIdAndSource(
            UUID userId,
            EntitlementSource source
    );

    @Query("""
        select grant
        from UserEntitlementGrant grant
        where grant.user.id = :userId
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
    List<UserEntitlementGrant>
    findAllActiveByUserId(
            UUID userId,
            Instant at
    );

    @Query("""
        select grant
        from UserEntitlementGrant grant
        where grant.user.id = :userId
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
    List<UserEntitlementGrant>
    findAllActiveByUserIdAndFeatureKey(
            UUID userId,
            FeatureKey featureKey,
            Instant at
    );

    void deleteAllByUserIdAndSource(
            UUID userId,
            EntitlementSource source
    );
}