package net.unibld.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

public class SessionDestroyedListener implements
    ApplicationListener<HttpSessionDestroyedEvent> {
	private static final Logger LOGGER=LoggerFactory.getLogger(SessionDestroyedListener.class);

	
	@Override
	public void onApplicationEvent(HttpSessionDestroyedEvent e) {
		String sid = e.getSession().getId();
		LOGGER.info("Session destroyed: {}",sid);
	}

}
