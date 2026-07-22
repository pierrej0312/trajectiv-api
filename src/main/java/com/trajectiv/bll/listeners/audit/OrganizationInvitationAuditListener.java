package com.trajectiv.bll.listeners.audit;

import com.trajectiv.bll.dto.audit.CreateAuditLogBllCommand;
import com.trajectiv.bll.events.organization.OrganizationInvitationAcceptedEvent;
import com.trajectiv.bll.events.organization.OrganizationInvitationAuditCreatedEvent;
import com.trajectiv.bll.events.organization.OrganizationInvitationResentEvent;
import com.trajectiv.bll.events.organization.OrganizationInvitationRevokedEvent;
import com.trajectiv.bll.services.audit.AuditLogService;
import com.trajectiv.dl.enums.audit.AuditAction;
import com.trajectiv.dl.enums.audit.AuditOutcome;
import com.trajectiv.dl.enums.audit.AuditTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationInvitationAuditListener {

    private final AuditLogService auditLogService;
    private final Clock clock;

    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onCreated(
            OrganizationInvitationAuditCreatedEvent event
    ) {
        auditLogService.record(
                command(
                        event.actorUserId(),
                        event.organizationId(),
                        AuditAction
                                .ORGANIZATION_INVITATION_CREATED,
                        event.invitationId(),
                        Map.of(
                                "invitedEmail",
                                event.invitedEmail(),
                                "role",
                                event.role().name()
                        )
                )
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onRevoked(
            OrganizationInvitationRevokedEvent event
    ) {
        auditLogService.record(
                command(
                        event.actorUserId(),
                        event.organizationId(),
                        AuditAction
                                .ORGANIZATION_INVITATION_REVOKED,
                        event.invitationId(),
                        Map.of(
                                "invitedEmail",
                                event.invitedEmail(),
                                "role",
                                event.role().name(),
                                "previousStatus",
                                "PENDING",
                                "newStatus",
                                "REVOKED"
                        )
                )
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onResent(
            OrganizationInvitationResentEvent event
    ) {
        auditLogService.record(
                command(
                        event.actorUserId(),
                        event.organizationId(),
                        AuditAction
                                .ORGANIZATION_INVITATION_RESENT,
                        event.newInvitationId(),
                        Map.of(
                                "previousInvitationId",
                                event.previousInvitationId()
                                        .toString(),
                                "newInvitationId",
                                event.newInvitationId()
                                        .toString(),
                                "invitedEmail",
                                event.invitedEmail(),
                                "role",
                                event.role().name()
                        )
                )
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onAccepted(
            OrganizationInvitationAcceptedEvent event
    ) {
        auditLogService.record(
                command(
                        event.actorUserId(),
                        event.organizationId(),
                        AuditAction
                                .ORGANIZATION_INVITATION_ACCEPTED,
                        event.invitationId(),
                        Map.of(
                                "membershipId",
                                event.membershipId().toString(),
                                "role",
                                event.role().name()
                        )
                )
        );
    }

    private CreateAuditLogBllCommand command(
            UUID actorUserId,
            UUID organizationId,
            AuditAction action,
            UUID invitationId,
            Map<String, Object> metadata
    ) {
        return new CreateAuditLogBllCommand(
                actorUserId,
                organizationId,
                action,
                AuditTargetType.ORGANIZATION_INVITATION,
                invitationId,
                AuditOutcome.SUCCESS,
                metadata,
                null,
                null,
                null,
                Instant.now(clock)
        );
    }
}