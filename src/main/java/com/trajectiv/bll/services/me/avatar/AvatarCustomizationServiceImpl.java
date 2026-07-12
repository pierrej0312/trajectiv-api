package com.trajectiv.bll.services.me.avatar;

import com.trajectiv.bll.dto.me.avatar.AvatarCustomizationBllDto;
import com.trajectiv.bll.dto.me.avatar.CreateAvatarCustomizationBllCommand;
import com.trajectiv.bll.dto.me.avatar.PatchAvatarCustomizationBllCommand;
import com.trajectiv.bll.exceptions.AvatarCustomizationAlreadyExistsException;
import com.trajectiv.bll.exceptions.AvatarCustomizationNotFoundException;
import com.trajectiv.bll.exceptions.InvalidAvatarCustomizationException;
import com.trajectiv.bll.exceptions.UserNotFoundException;
import com.trajectiv.bll.mappers.AvatarCustomizationBllMapper;
import com.trajectiv.config.security.AuthenticatedUserProvider;
import com.trajectiv.dl.entities.User;
import com.trajectiv.dl.entities.UserAvatarCustomization;
import com.trajectiv.dl.repositories.UserAvatarCustomizationRepository;
import com.trajectiv.dl.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvatarCustomizationServiceImpl implements AvatarCustomizationService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final UserRepository userRepository;
    private final UserAvatarCustomizationRepository avatarCustomizationRepository;
    private final AvatarCustomizationBllMapper avatarCustomizationBllMapper;

    @Override
    @Transactional
    public AvatarCustomizationBllDto createCurrentUserAvatarCustomization(
            Authentication authentication,
            CreateAvatarCustomizationBllCommand command
    ) {
        User currentUser = getCurrentUser(authentication);

        if (avatarCustomizationRepository.existsByUserId(currentUser.getId())) {
            throw new AvatarCustomizationAlreadyExistsException();
        }

        validateSkinIntensity(command.skinIntensity());
        validateHexColor(command.hairColor(), "hairColor");
        validateHexColor(command.beardColor(), "beardColor");

        UserAvatarCustomization customization = UserAvatarCustomization.create(
                currentUser,
                command.bodyType(),
                command.skinTone(),
                command.skinIntensity(),
                command.hairStyle(),
                command.hairColor().trim(),
                command.beardStyle(),
                command.beardColor().trim(),
                command.topStyle(),
                command.bottomStyle()
        );

        UserAvatarCustomization savedCustomization = avatarCustomizationRepository.save(customization);

        return avatarCustomizationBllMapper.toBllDto(savedCustomization);
    }

    @Override
    @Transactional(readOnly = true)
    public AvatarCustomizationBllDto getCurrentUserAvatarCustomization(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        UserAvatarCustomization customization = avatarCustomizationRepository.findByUserId(currentUser.getId())
                .orElseThrow(AvatarCustomizationNotFoundException::new);

        return avatarCustomizationBllMapper.toBllDto(customization);
    }

    @Override
    @Transactional
    public AvatarCustomizationBllDto patchCurrentUserAvatarCustomization(
            Authentication authentication,
            PatchAvatarCustomizationBllCommand command
    ) {
        User currentUser = getCurrentUser(authentication);

        UserAvatarCustomization customization = avatarCustomizationRepository.findByUserId(currentUser.getId())
                .orElseThrow(AvatarCustomizationNotFoundException::new);

        if (command.skinIntensity() != null) {
            validateSkinIntensity(command.skinIntensity());
        }

        if (command.hairColor() != null) {
            validateHexColor(command.hairColor(), "hairColor");
        }

        if (command.beardColor() != null) {
            validateHexColor(command.beardColor(), "beardColor");
        }

        customization.patch(
                command.bodyType(),
                command.skinTone(),
                command.skinIntensity(),
                command.hairStyle(),
                command.hairColor(),
                command.beardStyle(),
                command.beardColor(),
                command.topStyle(),
                command.bottomStyle()
        );

        return avatarCustomizationBllMapper.toBllDto(customization);
    }

    @Override
    @Transactional
    public void deleteCurrentUserAvatarCustomization(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        if (!avatarCustomizationRepository.existsByUserId(currentUser.getId())) {
            throw new AvatarCustomizationNotFoundException();
        }

        avatarCustomizationRepository.deleteByUserId(currentUser.getId());
    }

    private User getCurrentUser(Authentication authentication) {
        String keycloakSubject = authenticatedUserProvider
                .getClaims(authentication)
                .keycloakSubject();

        return userRepository.findByKeycloakSubject(keycloakSubject)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validateSkinIntensity(short skinIntensity) {
        if (skinIntensity < -2 || skinIntensity > 2) {
            throw new InvalidAvatarCustomizationException(
                    "skinIntensity must be between -2 and 2."
            );
        }
    }

    private void validateHexColor(String value, String fieldName) {
        if (value == null || !value.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new InvalidAvatarCustomizationException(
                    fieldName + " must be a valid hex color."
            );
        }
    }
}
