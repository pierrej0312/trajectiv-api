package com.trajectiv.dl.repositories.audit;

import com.trajectiv.dl.entities.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID> {
}