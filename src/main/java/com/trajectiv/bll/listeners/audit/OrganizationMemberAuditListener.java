package com.trajectiv.bll.listeners.audit;

import com.trajectiv.bll.dto.audit.CreateAuditLogBllCommand;
import com.trajectiv.bll.events.organization.member.OrganizationMemberRemovedEvent;
import com.trajectiv.bll.events.organization.member.OrganizationMemberRoleChangedEvent;
import com.trajectiv.bll.events.organization.member.OrganizationMemberStatusChangedEvent;
import com.trajectiv.bll.services.audit.AuditLogService;
import com.trajectiv.dl.enums.audit.AuditAction;
import com.trajectiv.dl.enums.audit.AuditOutcome;
import com.trajectiv.dl.enums.audit.AuditTargetType;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrganizationMemberAuditListener {

    private final AuditLogService auditLogService;
    private final Clock clock;

    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onRoleChanged(
            OrganizationMemberRoleChangedEvent event
    ) {
        auditLogService.record(
                command(
                        event.actorUserId(),
                        event.organizationId(),
                        AuditAction
                                .ORGANIZATION_MEMBER_ROLE_CHANGED,
                        event.memberId(),
                        Map.of(
                                "targetUserId",
                                event.targetUserId().toString(),
                                "previousRole",
                                event.previousRole().name(),
                                "newRole",
                                event.newRole().name()
                        )
                )
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onStatusChanged(
            OrganizationMemberStatusChangedEvent event
    ) {
        AuditAction action =
                event.newStatus() ==
                        OrganizationMemberStatus.SUSPENDED
                        ? AuditAction
                        .ORGANIZATION_MEMBER_SUSPENDED
                        : AuditAction
                        .ORGANIZATION_MEMBER_REACTIVATED;

        auditLogService.record(
                command(
                        event.actorUserId(),
                        event.organizationId(),
                        action,
                        event.memberId(),
                        Map.of(
                                "targetUserId",
                                event.targetUserId().toString(),
                                "previousStatus",
                                event.previousStatus().name(),
                                "newStatus",
                                event.newStatus().name()
                        )
                )
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onRemoved(
            OrganizationMemberRemovedEvent event
    ) {
        auditLogService.record(
                command(
                        event.actorUserId(),
                        event.organizationId(),
                        AuditAction
                                .ORGANIZATION_MEMBER_REMOVED,
                        event.memberId(),
                        Map.of(
                                "targetUserId",
                                event.targetUserId().toString(),
                                "previousRole",
                                event.previousRole().name(),
                                "previousStatus",
                                event.previousStatus().name(),
                                "newStatus",
                                OrganizationMemberStatus
                                        .REMOVED
                                        .name()
                        )
                )
        );
    }

    private CreateAuditLogBllCommand command(
            java.util.UUID actorUserId,
            java.util.UUID organizationId,
            AuditAction action,
            java.util.UUID memberId,
            Map<String, Object> metadata
    ) {
        return new CreateAuditLogBllCommand(
                actorUserId,
                organizationId,
                action,
                AuditTargetType.ORGANIZATION_MEMBER,
                memberId,
                AuditOutcome.SUCCESS,
                metadata,
                null,
                null,
                null,
                Instant.now(clock)
        );
    }
}