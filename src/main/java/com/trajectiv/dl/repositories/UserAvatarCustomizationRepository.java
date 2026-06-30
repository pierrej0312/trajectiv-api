package com.trajectiv.dl.repositories;

import com.trajectiv.dl.entities.UserAvatarCustomization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAvatarCustomizationRepository extends JpaRepository<UserAvatarCustomization, UUID> {

    Optional<UserAvatarCustomization> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
