package net.unibld.server.service.security.mail;

import net.unibld.server.service.mail.MailCallback;
import net.unibld.server.service.security.SecurityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivationMailCallback implements MailCallback {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueuedSecurityMailServiceImpl.class);
	
	@Autowired
	private SecurityService securityService;
	
	@Override
	public void sendCompleted(String param) {
		securityService.updateActivationMailSent(param);
		LOGGER.info("Updated user on ActivationMailCallback: "+param);
	}

	@Override
	public void sendFailed(String param) {
		// do nothing
		
	}

}
