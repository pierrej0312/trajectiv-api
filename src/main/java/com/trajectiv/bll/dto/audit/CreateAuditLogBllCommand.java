package com.trajectiv.bll.dto.audit;

import com.trajectiv.dl.enums.audit.AuditAction;
import com.trajectiv.dl.enums.audit.AuditOutcome;
import com.trajectiv.dl.enums.audit.AuditTargetType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CreateAuditLogBllCommand(
        UUID actorUserId,
        UUID organizationId,
        AuditAction action,
        AuditTargetType targetType,
        UUID targetId,
        AuditOutcome outcome,
        Map<String, Object> metadata,
        String correlationId,
        String ipAddress,
        String userAgent,
        Instant occurredAt
) {
}