package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.organization.OrganizationRoleApiDto;
import com.trajectiv.api.dto.organization.member.OrganizationMemberResponseApiDto;
import com.trajectiv.api.dto.organization.member.OrganizationMemberStatusApiDto;
import com.trajectiv.api.dto.organization.member.UpdateOrganizationMemberRoleRequestApiDto;
import com.trajectiv.bll.dto.organization.member.OrganizationMemberBllDto;
import com.trajectiv.bll.dto.organization.member.UpdateOrganizationMemberRoleBllCommand;
import com.trajectiv.dl.enums.organization.OrganizationMemberStatus;
import com.trajectiv.dl.enums.organization.OrganizationRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrganizationMemberApiMapper {

    public OrganizationMemberResponseApiDto toApiDto(
            OrganizationMemberBllDto member
    ) {
        return new OrganizationMemberResponseApiDto(
                member.id(),
                member.userId(),
                member.email(),
                member.firstName(),
                member.lastName(),
                member.displayName(),
                toApiRole(member.role()),
                mapStatus(member.status()),
                member.joinedAt(),
                member.createdAt(),
                member.updatedAt()
        );
    }

    public List<OrganizationMemberResponseApiDto> toApiDtos(
            List<OrganizationMemberBllDto> members
    ) {
        if (members == null || members.isEmpty()) {
            return List.of();
        }

        return members
                .stream()
                .map(this::toApiDto)
                .toList();
    }

    private OrganizationMemberStatusApiDto mapStatus(
            OrganizationMemberStatus status
    ) {
        return switch (status) {
            case ACTIVE ->
                    OrganizationMemberStatusApiDto.ACTIVE;

            case SUSPENDED ->
                    OrganizationMemberStatusApiDto.SUSPENDED;

            case REMOVED ->
                    throw new IllegalArgumentException(
                            "A removed organization member cannot be exposed."
                    );
        };
    }

    public UpdateOrganizationMemberRoleBllCommand toBllCommand(
            UpdateOrganizationMemberRoleRequestApiDto request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "request cannot be null."
            );
        }

        return new UpdateOrganizationMemberRoleBllCommand(
                toDomainRole(request.role())
        );
    }

    private OrganizationRoleApiDto toApiRole(
            OrganizationRole role
    ) {
        return switch (role) {
            case ORGANIZATION_OWNER ->
                    OrganizationRoleApiDto.ORGANIZATION_OWNER;

            case ORGANIZATION_ADMIN ->
                    OrganizationRoleApiDto.ORGANIZATION_ADMIN;

            case RECRUITER ->
                    OrganizationRoleApiDto.RECRUITER;

            case COACH ->
                    OrganizationRoleApiDto.COACH;

            case TRAINER ->
                    OrganizationRoleApiDto.TRAINER;

            case LEARNER ->
                    OrganizationRoleApiDto.LEARNER;
        };
    }

    private OrganizationRole toDomainRole(
            OrganizationRoleApiDto role
    ) {
        return switch (role) {
            case ORGANIZATION_OWNER ->
                    OrganizationRole.ORGANIZATION_OWNER;

            case ORGANIZATION_ADMIN ->
                    OrganizationRole.ORGANIZATION_ADMIN;

            case RECRUITER ->
                    OrganizationRole.RECRUITER;

            case COACH ->
                    OrganizationRole.COACH;

            case TRAINER ->
                    OrganizationRole.TRAINER;

            case LEARNER ->
                    OrganizationRole.LEARNER;
        };
    }
}