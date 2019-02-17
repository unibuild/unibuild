package net.unibld.core.config;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.unibld.core.ServerType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "instances")
public class InstanceConfigurations implements Serializable {
	@XmlElement(required=true,name="instance")
	private List<InstanceConfig> instanceConfigs;
	
	
	@XmlAttribute(required=false)
	private String defaultNode;
	
	public List<InstanceConfig> getInstanceConfigs() {
		return instanceConfigs;
	}

	public void setInstanceConfigs(List<InstanceConfig> instanceConfigs) {
		this.instanceConfigs = instanceConfigs;
	}
	
	public String getDefaultNode() {
		return defaultNode;
	}

	public void setDefaultNode(String defaultNode) {
		this.defaultNode = defaultNode;
	}

	
}
