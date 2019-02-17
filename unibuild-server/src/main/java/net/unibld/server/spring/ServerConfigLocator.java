package net.unibld.server.spring;

import java.io.File;

import net.unibld.core.util.GlobalConfigExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("serverConfigLocator")
public class ServerConfigLocator {
	private static final Logger LOGGER=LoggerFactory.getLogger(ServerConfigLocator.class);
	
	
	public String getPropertiesFileLocation() {
		String path = getPropertiesFile();
		
		if (path.startsWith("/")) {
			return "file:"+path;
		} else {
			return "file:/"+path;
		}
	}

	protected String getPropertiesFile() {
		String defaultConfigPath = GlobalConfigExtractor.getDefaultConfigPath();
		if (defaultConfigPath==null) {
			throw new IllegalStateException("Default config path not specified");
		}
		File cfg=new File(defaultConfigPath);
		
		if (!cfg.exists()||!cfg.isFile()) {
			try {
				ServerConfigExtractor.extract(null);
			} catch (Exception e) {
				LOGGER.error("Failed to extract global config to: "+defaultConfigPath,e);
				throw new IllegalStateException("Failed to extract global config to: "+defaultConfigPath,e);
			}
			
		}
		return defaultConfigPath;
	} 
}
