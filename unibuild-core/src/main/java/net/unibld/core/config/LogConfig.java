package net.unibld.core.config;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Configures the build loggers used. A log configuration can have several loggers
 * that log the events of a build session.
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "logConfig")
public class LogConfig implements Serializable {
	
	private static final long serialVersionUID = 6920172057661462794L;

	@XmlElement(required=true,name="logger")
	private List<LoggerConfig> loggerConfigs;

	@XmlAttribute(required=false)
	private boolean replaceGlobal;
	
	public LogConfig(){

	}

	

	public List<LoggerConfig> getLoggerConfigs() {
		return loggerConfigs;
	}

	public void setLoggerConfigs(List<LoggerConfig> loggerConfigs) {
		this.loggerConfigs = loggerConfigs;
	}

	public boolean isReplaceGlobal() {
		return replaceGlobal;
	}

	public void setReplaceGlobal(boolean replaceGlobal) {
		this.replaceGlobal = replaceGlobal;
	}
}//end LogConfig