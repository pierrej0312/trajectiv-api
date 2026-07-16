package com.trajectiv.dl.repositories.credits;

import com.trajectiv.dl.entities.credits.OrganizationAiCreditWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationAiCreditWalletRepository
        extends JpaRepository<
        OrganizationAiCreditWallet,
        UUID
        > {

    Optional<OrganizationAiCreditWallet>
    findByOrganizationId(
            UUID organizationId
    );

    boolean existsByOrganizationId(
            UUID organizationId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select wallet
        from OrganizationAiCreditWallet wallet
        where wallet.organization.id = :organizationId
        """)
    Optional<OrganizationAiCreditWallet>
    findByOrganizationIdForUpdate(
            UUID organizationId
    );
}