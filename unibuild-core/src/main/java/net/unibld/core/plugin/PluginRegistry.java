package net.unibld.core.plugin;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A Spring component that holds available plugins during runtime.
 * @author andor
 *
 */
@Component("pluginRegistry")
public class PluginRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginRegistry.class);

	private Map<String,Plugin> registry=new Hashtable<String, Plugin>();
	
	public boolean isPluginExisting(String name) {
		return registry.containsKey(name);
	}
	
	public void addPlugin(Plugin p) {
		if (p==null) {
			throw new IllegalArgumentException("Plugin was null");
		}
		if (p.getName()==null) {
			throw new IllegalArgumentException("Plugin name was null");
		}
		if (isPluginExisting(p.getName())) {
			throw new IllegalStateException("Plugin already exists: "+p.getName());
		}
		
		registry.put(p.getName(), p);
		LOGGER.info("Plugin added: {}",p.getName());
	}
	
	public Set<String> getPluginNames() {
		return registry.keySet();
	}
 }
