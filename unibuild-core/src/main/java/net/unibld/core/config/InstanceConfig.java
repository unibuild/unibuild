package net.unibld.core.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.unibld.core.BuildProject;
import net.unibld.core.persistence.model.Project;

/**
 * Defines a server instance for a {@link BuildProject}. Typical server instance types are
 * live server, test server or staging server.<br>
 * Each server instance has a unique name in the {@link Project} and a dedicated
 * {@link ServerConfig} object to provide server control methods for server instance.
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:24
 */
@XmlRootElement(name="instanceConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstanceConfig implements Serializable {
	
	private static final long serialVersionUID = -3259305543897770311L;
	@XmlAttribute(name="name",required=true)
	private String nodeName;
	@XmlElement(name="server",required=true)
	private ServerConfig serverConfig;

	
	

	/**
	 * @return Instance node name
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName Instance node name
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * @return Server config element
	 */
	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	/**
	 * @param serverConfig Server config element
	 */
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
}//end InstanceConfig