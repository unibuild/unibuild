package net.unibld.server.service.plugin;

import javax.annotation.PostConstruct;

import net.unibld.core.plugin.PluginDetector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Spring plugin service implementation for initializing plugins available on the server side.
 * @author andor
 *
 */
@Service
public class PluginServiceImpl implements PluginService {
	private static final Logger LOGGER=LoggerFactory.getLogger(PluginServiceImpl.class);
	
	@Autowired
	private PluginDetector pluginDetector;
	
	@PostConstruct
	public void init() {
		LOGGER.info("Initializing plugins available for the server...");
		pluginDetector.detectPlugins();
	}
}
