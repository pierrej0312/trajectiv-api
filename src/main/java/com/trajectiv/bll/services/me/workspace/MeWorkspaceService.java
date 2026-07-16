package com.trajectiv.bll.services.me.workspace;

import com.trajectiv.bll.dto.me.workspace.MeWorkspaceBllDto;

import java.util.List;
import java.util.UUID;

public interface MeWorkspaceService {

    List<MeWorkspaceBllDto> getWorkspaces(
            UUID userId
    );
}