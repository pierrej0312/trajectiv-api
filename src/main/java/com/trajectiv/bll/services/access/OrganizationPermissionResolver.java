package com.trajectiv.bll.services.access;

import com.trajectiv.bll.dto.access.OrganizationPermission;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
public class OrganizationPermissionResolver {

    public Set<OrganizationPermission> resolve(
            OrganizationRole role
    ) {
        if (role == null) {
            return Set.of();
        }

        return switch (role) {
            case ORGANIZATION_OWNER ->
                    Set.copyOf(
                            EnumSet.allOf(
                                    OrganizationPermission.class
                            )
                    );

            case ORGANIZATION_ADMIN ->
                    Set.copyOf(
                            EnumSet.of(
                                    OrganizationPermission
                                            .ORGANIZATION_READ,
                                    OrganizationPermission
                                            .ORGANIZATION_UPDATE,
                                    OrganizationPermission
                                            .MEMBER_READ,
                                    OrganizationPermission
                                            .MEMBER_INVITE,
                                    OrganizationPermission
                                            .MEMBER_UPDATE_ROLE,
                                    OrganizationPermission
                                            .MEMBER_SUSPEND,
                                    OrganizationPermission
                                            .MEMBER_REMOVE,
                                    OrganizationPermission
                                            .COHORT_READ,
                                    OrganizationPermission
                                            .COHORT_MANAGE,
                                    OrganizationPermission
                                            .LEARNER_READ,
                                    OrganizationPermission
                                            .TRAINING_ASSIGN,
                                    OrganizationPermission
                                            .REPORT_READ
                            )
                    );

            case RECRUITER ->
                    Set.copyOf(
                            EnumSet.of(
                                    OrganizationPermission
                                            .ORGANIZATION_READ,
                                    OrganizationPermission
                                            .MEMBER_READ,
                                    OrganizationPermission
                                            .LEARNER_READ,
                                    OrganizationPermission
                                            .REPORT_READ
                            )
                    );

            case COACH, TRAINER ->
                    Set.copyOf(
                            EnumSet.of(
                                    OrganizationPermission
                                            .ORGANIZATION_READ,
                                    OrganizationPermission
                                            .COHORT_READ,
                                    OrganizationPermission
                                            .LEARNER_READ,
                                    OrganizationPermission
                                            .TRAINING_ASSIGN
                            )
                    );

            case LEARNER ->
                    Set.of(
                            OrganizationPermission
                                    .ORGANIZATION_READ
                    );
        };
    }
}