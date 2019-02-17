package net.unibld.server.spring;

import net.unibld.server.service.security.SecurityService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

public class SpringLoginListener implements ApplicationListener<AuthenticationSuccessEvent>{
	private static final Log LOG=LogFactory.getLog(SpringLoginListener.class);
	@Autowired
	private SecurityService securityManager;
	
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent arg0) {
		String userId = arg0.getAuthentication().getName();
		
		try {
			securityManager.updateLastLogin(userId);
			LOG.debug("Successful login from web: "+userId);
		} catch (Exception e) {
			LOG.error("Failed to update last login: "+userId,e);
		}
		
	}

}
