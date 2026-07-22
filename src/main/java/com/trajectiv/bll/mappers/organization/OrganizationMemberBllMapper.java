package com.trajectiv.bll.mappers.organization;

import com.trajectiv.bll.dto.organization.member.OrganizationMemberBllDto;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.organization.OrganizationMember;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class OrganizationMemberBllMapper {

    public OrganizationMemberBllDto toDto(
            OrganizationMember member
    ) {
        Objects.requireNonNull(
                member,
                "member cannot be null."
        );

        User user = member.getUser();

        return new OrganizationMemberBllDto(
                member.getId(),
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDisplayName(),
                member.getRole(),
                member.getStatus(),
                member.getJoinedAt(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }

    public List<OrganizationMemberBllDto> toDtos(
            List<OrganizationMember> members
    ) {
        if (members == null || members.isEmpty()) {
            return List.of();
        }

        return members
                .stream()
                .map(this::toDto)
                .toList();
    }
}