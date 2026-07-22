package com.trajectiv.bll.services.audit;

import com.trajectiv.bll.dto.audit.CreateAuditLogBllCommand;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.audit.AuditLog;
import com.trajectiv.dl.entities.organization.Organization;
import com.trajectiv.dl.repositories.UserRepository;
import com.trajectiv.dl.repositories.audit.AuditLogRepository;
import com.trajectiv.dl.repositories.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl
        implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public void record(
            CreateAuditLogBllCommand command
    ) {
        Objects.requireNonNull(
                command,
                "command cannot be null."
        );

        User actor =
                command.actorUserId() == null
                        ? null
                        : userRepository
                        .getReferenceById(
                                command.actorUserId()
                        );

        Organization organization =
                command.organizationId() == null
                        ? null
                        : organizationRepository
                        .getReferenceById(
                                command.organizationId()
                        );

        auditLogRepository.save(
                AuditLog.create(
                        actor,
                        organization,
                        command.action(),
                        command.targetType(),
                        command.targetId(),
                        command.outcome(),
                        command.metadata(),
                        command.correlationId(),
                        command.ipAddress(),
                        command.userAgent(),
                        command.occurredAt()
                )
        );
    }
}