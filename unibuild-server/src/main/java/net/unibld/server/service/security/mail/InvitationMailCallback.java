package net.unibld.server.service.security.mail;

import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.service.mail.MailCallback;
import net.unibld.server.service.security.invitation.InvitationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationMailCallback implements MailCallback {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueuedSecurityMailServiceImpl.class);
	
	@Autowired
	private InvitationService invitationService;
	
	@Override
	public void sendCompleted(String param) {
		Invitation inv=invitationService.findInvitation(Long.parseLong(param.trim()));
		if (inv==null) {
			throw new IllegalArgumentException("Invalid invitation id: "+param);
		}
		invitationService.updateInvitationSent(inv);
		
		LOGGER.info("Updated invitation on InvitationMailCallback: "+param);
	}

	@Override
	public void sendFailed(String param) {
		// do nothing
		Invitation inv=invitationService.findInvitation(Long.parseLong(param.trim()));
		if (inv==null) {
			throw new IllegalArgumentException("Invalid invitation id: "+param);
		}
		invitationService.updateInvitationError(inv);
		
		LOGGER.info("Updated failed invitation on InvitationMailCallback: "+param);
	}

}
