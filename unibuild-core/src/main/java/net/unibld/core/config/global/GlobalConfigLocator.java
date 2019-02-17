package net.unibld.core.config.global;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.util.GlobalConfigExtractor;

@Component("globalConfigLocator")
public class GlobalConfigLocator {
	private static final Logger LOGGER=LoggerFactory.getLogger(GlobalConfigLocator.class);
	
	
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
				GlobalConfigExtractor.extract(null);
			} catch (Exception e) {
				LOGGER.error("Failed to extract global config to: "+defaultConfigPath,e);
				throw new IllegalStateException("Failed to extract global config to: "+defaultConfigPath,e);
			}
			
		}
		return defaultConfigPath;
	} 
}
