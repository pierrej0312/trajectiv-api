package com.trajectiv.bll.services.notification;

import com.trajectiv.bll.dto.notification.OrganizationInvitationEmailBllDto;
import com.trajectiv.config.mail.MailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpTransactionalEmailService
        implements TransactionalEmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendOrganizationInvitation(
            OrganizationInvitationEmailBllDto email
    ) {
        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(
                mailProperties.fromAddress()
        );

        message.setTo(
                email.recipientEmail()
        );

        message.setSubject(
                "Invitation à rejoindre "
                        + email.organizationName()
                        + " sur Trajectiv"
        );

        message.setText(
                """
                Bonjour,

                %s vous invite à rejoindre %s sur Trajectiv.

                Rôle proposé : %s

                Pour accepter l'invitation :
                %s

                Cette invitation expire le %s.

                L'équipe Trajectiv
                """.formatted(
                        email.inviterDisplayName(),
                        email.organizationName(),
                        email.roleLabel(),
                        email.acceptanceUrl(),
                        email.expiresAt()
                )
        );

        mailSender.send(message);
    }
}