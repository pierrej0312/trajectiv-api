package com.trajectiv.bll.services.audit;

import com.trajectiv.bll.dto.audit.CreateAuditLogBllCommand;

public interface AuditLogService {

    void record(
            CreateAuditLogBllCommand command
    );
}