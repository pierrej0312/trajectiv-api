package com.trajectiv.repositories;

import com.trajectiv.dl.entities.UserFile;
import com.trajectiv.dl.enums.FileStatus;
import com.trajectiv.dl.enums.UserFileKind;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserFileRepository extends JpaRepository<UserFile, UUID> {

    Optional<UserFile> findByStorageKey(String storageKey);

    List<UserFile> findByOwnerUserIdAndKindAndDeletedAtIsNull(
            UUID ownerUserId,
            UserFileKind kind
    );

    List<UserFile> findByOwnerUserIdAndStatusAndDeletedAtIsNull(
            UUID ownerUserId,
            FileStatus status
    );
}
