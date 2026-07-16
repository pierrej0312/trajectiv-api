package com.trajectiv.bll.services.notification;

import com.trajectiv.bll.dto.notification.OrganizationInvitationEmailBllDto;

public interface TransactionalEmailService {

    void sendOrganizationInvitation(
            OrganizationInvitationEmailBllDto email
    );
}