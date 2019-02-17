package net.unibld.core.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.unibld.core.Parameter;

/**
 * Defines the configuration of a single build logger that uses a single channel
 * (appender) to log the messages received.
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:27
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loggerConfig")
public class LoggerConfig implements Serializable {

	@XmlElement
	private String loggerClass;
	@XmlTransient
	private Map<String,String> parameterMap;

	@XmlElement(name="parameter")
	private List<Parameter> parameters;
	
	public LoggerConfig(){

	}

	public void finalize() throws Throwable {

	}

	public String getLoggerClass() {
		return loggerClass;
	}

	public void setLoggerClass(String loggerClass) {
		this.loggerClass = loggerClass;
	}

	public void addParameter(String name,String value) {
		if (this.parameters==null) {
			parameters=new ArrayList<Parameter>();
		}
		if (parameterMap==null) {
			parameterMap=new HashMap<String, String>();
		}
		parameterMap.put(name,value);
		Parameter p = new Parameter();
		p.setName(name);
		p.setValue(value);
		parameters.add(p);
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	
}//end LoggerConfig