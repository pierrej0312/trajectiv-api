package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.organization.CreateOrganizationRequestApiDto;
import com.trajectiv.api.dto.organization.OrganizationResponseApiDto;
import com.trajectiv.api.dto.organization.OrganizationStatusApiDto;
import com.trajectiv.api.dto.organization.UpdateOrganizationRequestApiDto;
import com.trajectiv.bll.dto.organization.CreateOrganizationBllCommand;
import com.trajectiv.bll.dto.organization.OrganizationBllDto;
import com.trajectiv.bll.dto.organization.UpdateOrganizationBllCommand;
import com.trajectiv.dl.enums.organization.OrganizationStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrganizationApiMapper {

    public CreateOrganizationBllCommand toBllCommand(
            CreateOrganizationRequestApiDto request
    ) {
        Objects.requireNonNull(
                request,
                "request cannot be null."
        );

        return new CreateOrganizationBllCommand(
                request.slug(),
                request.name()
        );
    }

    public UpdateOrganizationBllCommand toBllCommand(
            UpdateOrganizationRequestApiDto request
    ) {
        Objects.requireNonNull(
                request,
                "request cannot be null."
        );

        return new UpdateOrganizationBllCommand(
                request.name(),
                request.avatarUrl()
        );
    }

    public OrganizationResponseApiDto toApiDto(
            OrganizationBllDto organization
    ) {
        Objects.requireNonNull(
                organization,
                "organization cannot be null."
        );

        return new OrganizationResponseApiDto(
                organization.id(),
                organization.slug(),
                organization.name(),
                organization.avatarUrl(),
                mapStatus(organization.status()),
                organization.createdAt(),
                organization.updatedAt()
        );
    }

    private OrganizationStatusApiDto mapStatus(
            OrganizationStatus status
    ) {
        return switch (status) {
            case ACTIVE ->
                    OrganizationStatusApiDto.ACTIVE;

            case SUSPENDED ->
                    OrganizationStatusApiDto.SUSPENDED;

            case ARCHIVED ->
                    OrganizationStatusApiDto.ARCHIVED;
        };
    }
}