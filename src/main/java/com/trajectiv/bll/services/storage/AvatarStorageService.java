package com.trajectiv.bll.services.storage;

import com.trajectiv.bll.dto.storage.StoredAvatarBllDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarStorageService {

    StoredAvatarBllDto uploadCurrentUserAvatar(Authentication authentication, MultipartFile file);

    StoredAvatarBllDto deleteCurrentUserAvatar(Authentication authentication);
}
