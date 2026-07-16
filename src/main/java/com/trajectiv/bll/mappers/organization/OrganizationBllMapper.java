package com.trajectiv.bll.mappers.organization;

import com.trajectiv.bll.dto.organization.OrganizationBllDto;
import com.trajectiv.dl.entities.organization.Organization;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrganizationBllMapper {

    public OrganizationBllDto toDto(
            Organization organization
    ) {
        Objects.requireNonNull(
                organization,
                "organization cannot be null."
        );

        return new OrganizationBllDto(
                organization.getId(),
                organization.getSlug(),
                organization.getName(),
                organization.getAvatarUrl(),
                organization.getStatus(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }
}