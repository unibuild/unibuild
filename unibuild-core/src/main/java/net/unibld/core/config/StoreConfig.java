package net.unibld.core.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.unibld.core.security.PasswordStrategy;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "store")
public class StoreConfig implements Serializable {
	
	@XmlAttribute(name="strategy",required=true)
	private PasswordStrategy strategy;
	
	
	@XmlElement(name="location",required=false)
	private String location;
	@XmlElement(name="class",required=true)
	private String className;
	

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public PasswordStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(PasswordStrategy strategy) {
		this.strategy = strategy;
	}

	

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
