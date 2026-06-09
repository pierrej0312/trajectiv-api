package com.trajectiv.bll.mappers;

import com.trajectiv.bll.dto.me.UserBllDto;
import com.trajectiv.dl.entities.User;
import org.springframework.stereotype.Component;


@Component
public class UserBllMapper {

    public UserBllDto toDto(User user) {
        return new UserBllDto(
                user.getId(),
                user.getKeycloakSubject(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getFirstName(),
                user.getLastName(),
                user.getDisplayName(),
                user.getStatus()
        );
    }
}
