package com.trajectiv.dl.repositories;

import com.trajectiv.dl.entities.AiCreditWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AiCreditWalletRepository extends JpaRepository<AiCreditWallet, UUID> {

    Optional<AiCreditWallet> findByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select wallet
        from AiCreditWallet wallet
        where wallet.user.id = :userId
        """)
    Optional<AiCreditWallet> findByUserIdForUpdate(
            @Param("userId") UUID userId
    );

    boolean existsByUserId(UUID userId);
}
