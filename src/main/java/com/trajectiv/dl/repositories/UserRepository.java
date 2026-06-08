package com.trajectiv.dl.repositories;

import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKeycloakSubject(String keycloakSubject);

    Optional<User> findByEmail(String email);

    boolean existsByKeycloakSubject(String keycloakSubject);

    boolean existsByEmail(String email);

    long countByStatus(UserStatus status);
}
