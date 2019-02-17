package net.unibld.server.service.plugin;

import javax.annotation.PostConstruct;

/**
 * Plugin service interface for initializing plugins available on the server side.
 * @author andor
 *
 */
public interface PluginService {
	/**
	 * Initializes available plugins, typically on {@link PostConstruct}
	 */
	void init();
}
