package net.unibld.core.config.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.stereotype.Component;

import net.unibld.core.config.LoggingConfiguration;
import net.unibld.core.util.PropertyPlaceHolderUtils;


/**
 * A global configuration for customizing the build tool itself. Project
 * configuration settings can override the values defined here.
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:22
 */
@Component("globalConfig")
public class GlobalConfig {

	@Autowired
	private AbstractBeanFactory beanFactory;

	

	@Autowired
	private LoggingConfiguration loggingConfig;
	
	/**
	 * @return Logging configuration
	 */
	public LoggingConfiguration getLoggingConfig() {
		return loggingConfig;
	}

	
	
	/**
	 * @return The name of the software
	 */
	public static String getSoftwareName() {
		return "unibuild";
	}

	/**
	 * Returns the value of a property in the global settings
	 * @param name Name of the property
	 * @return Value of the property or null
	 */
	public String getPropertyAsString(String name) {
		return PropertyPlaceHolderUtils.getProperty(beanFactory, name);
	}

}//end GlobalConfig