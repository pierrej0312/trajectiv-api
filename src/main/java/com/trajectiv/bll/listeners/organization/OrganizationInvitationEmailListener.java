package com.trajectiv.bll.listeners.organization;

import com.trajectiv.bll.events.organization.OrganizationInvitationCreatedEvent;
import com.trajectiv.bll.services.notification.TransactionalEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrganizationInvitationEmailListener {

    private final TransactionalEmailService emailService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onInvitationCreated(
            OrganizationInvitationCreatedEvent event
    ) {
        emailService.sendOrganizationInvitation(
                event.email()
        );
    }
}