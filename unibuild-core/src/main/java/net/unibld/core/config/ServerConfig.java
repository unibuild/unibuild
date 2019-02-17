package net.unibld.core.config;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.unibld.core.ServerDirectory;
import net.unibld.core.ServerType;

/**
 * Represents the configuration of a physical server instance that is dedicated to
 * a BuildProject using an InstanceConfig object.
 * 
 * Each physical instance can represent one logical server instance in a project
 * and each physical instance should be used in only one project, unless the user
 * (or the server itself) takes care of concurrent deployments to the server
 * (typical in EJB containers)
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:31
 */
@XmlRootElement(name="serverConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerConfig implements Serializable {
	@XmlElement(required=false,name="dirToClean")
	private List<ServerDirectory> dirsToClean;
	@XmlElement(name="path",required=true)
	private String serverPath;
	@XmlElement(name="restartScript",required=false)
	private String serverRestartScript;
	@XmlElement(name="startScript",required=true)
	private String serverStartScript;
	@XmlElement(name="stopScript",required=true)
	private String serverStopScript;


	public ServerConfig(){

	}

	public void finalize() throws Throwable {

	}

	

	public List<ServerDirectory> getDirsToClean() {
		return dirsToClean;
	}

	public void setDirsToClean(List<ServerDirectory> dirsToClean) {
		this.dirsToClean = dirsToClean;
	}


	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public String getServerRestartScript() {
		return serverRestartScript;
	}

	public void setServerRestartScript(String serverRestartScript) {
		this.serverRestartScript = serverRestartScript;
	}

	public String getServerStartScript() {
		return serverStartScript;
	}

	public void setServerStartScript(String serverStartScript) {
		this.serverStartScript = serverStartScript;
	}

	public String getServerStopScript() {
		return serverStopScript;
	}

	public void setServerStopScript(String serverStopScript) {
		this.serverStopScript = serverStopScript;
	}

	
}//end ServerConfig