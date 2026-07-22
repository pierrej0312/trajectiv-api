package com.trajectiv.dl.entities.audit;

import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.organization.Organization;
import com.trajectiv.dl.enums.audit.AuditAction;
import com.trajectiv.dl.enums.audit.AuditOutcome;
import com.trajectiv.dl.enums.audit.AuditTargetType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(
                        name = "idx_audit_logs_actor_user_id",
                        columnList = "actor_user_id"
                ),
                @Index(
                        name = "idx_audit_logs_organization_id",
                        columnList = "organization_id"
                ),
                @Index(
                        name = "idx_audit_logs_action",
                        columnList = "action"
                ),
                @Index(
                        name = "idx_audit_logs_target",
                        columnList = "target_type,target_id"
                ),
                @Index(
                        name = "idx_audit_logs_occurred_at",
                        columnList = "occurred_at"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "actor_user_id",
            foreignKey = @ForeignKey(
                    name = "fk_audit_logs_actor_user"
            )
    )
    private User actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organization_id",
            foreignKey = @ForeignKey(
                    name = "fk_audit_logs_organization"
            )
    )
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 100
    )
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "target_type",
            nullable = false,
            length = 80
    )
    private AuditTargetType targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private AuditOutcome outcome;

    @JdbcTypeCode(
            org.hibernate.type.SqlTypes.JSON
    )
    @Column(
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> metadata;

    @Column(
            name = "correlation_id",
            length = 100
    )
    private String correlationId;

    @Column(
            name = "ip_address",
            length = 64
    )
    private String ipAddress;

    @Column(
            name = "user_agent",
            length = 512
    )
    private String userAgent;

    @Column(
            name = "occurred_at",
            nullable = false
    )
    private Instant occurredAt;

    public static AuditLog create(
            User actor,
            Organization organization,
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
        Objects.requireNonNull(
                action,
                "action cannot be null."
        );

        Objects.requireNonNull(
                targetType,
                "targetType cannot be null."
        );

        Objects.requireNonNull(
                outcome,
                "outcome cannot be null."
        );

        Objects.requireNonNull(
                occurredAt,
                "occurredAt cannot be null."
        );

        AuditLog auditLog = new AuditLog();

        auditLog.id = UUID.randomUUID();
        auditLog.actor = actor;
        auditLog.organization = organization;
        auditLog.action = action;
        auditLog.targetType = targetType;
        auditLog.targetId = targetId;
        auditLog.outcome = outcome;
        auditLog.metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
        auditLog.correlationId = correlationId;
        auditLog.ipAddress = ipAddress;
        auditLog.userAgent = userAgent;
        auditLog.occurredAt = occurredAt;

        return auditLog;
    }
}