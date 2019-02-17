package net.unibld.core;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contextAttribute")
public class ContextAttribute implements Serializable {
	@XmlAttribute
	private String name;
	@XmlValue
	private String value;
	@XmlTransient
	private Serializable serializable;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Serializable getSerializable() {
		return serializable;
	}
	public void setSerializable(Serializable serializable) {
		this.serializable = serializable;
	}
}
