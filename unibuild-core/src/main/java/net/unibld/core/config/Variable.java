package net.unibld.core.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Defines a user-defined variable that will be substituted throughout the
 * whole project
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:24
 */
@XmlRootElement(name="var")
@XmlAccessorType(XmlAccessType.FIELD)
public class Variable implements Serializable {
	@XmlAttribute(name="name",required=true)
	private String name;
	
	@XmlAttribute(name="defaultValue",required=false)
	private String defaultValue;
	
	@XmlAttribute(name="switch",required=false)
	private boolean switchType;

	/**
	 * @return Variable name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Variable name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Default value defined by the user in project.xml
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue Default value defined by the user in project.xml
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return True if this is a boolean switch
	 */
	public boolean isSwitchType() {
		return switchType;
	}

	/**
	 * @param switchType True if this is a boolean switch
	 */
	public void setSwitchType(boolean switchType) {
		this.switchType = switchType;
	}
	

	


}//end Variable