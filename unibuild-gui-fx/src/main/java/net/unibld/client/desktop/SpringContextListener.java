package net.unibld.client.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.unibld.core.plugin.PluginDetector;

@Component
public class SpringContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringContextListener.class);

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private PluginDetector pluginDetector;
	
	@EventListener({ContextRefreshedEvent.class})
    public void contextRefreshedEvent() {
        LOGGER.info("Spring context refreshed.");
        SpringBeanFactory.contextInitialized(context);
        
        pluginDetector.detectPlugins();
		
    }
}
