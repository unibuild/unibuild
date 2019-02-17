package net.unibld.core.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Configuration class that configures a BuildProject.
 * 
 * Each project configuration contains a BuildConfig and a VersionConfig object
 * configuring the build process and the project version respectively, and a list
 * of InstanceConfig objects that define available server instances for the
 * project.
 * 
 * ProjectConfig is initialized using a configuration file called project.xml in
 * the project folder. Project folders must reside in the workspace folder of the
 * tool.
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:30
 */
@XmlRootElement(name="project")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectConfig implements Serializable{
	
	private static final long serialVersionUID = -8001659886668234473L;

	@XmlAttribute
	private String companyName;
	
	@XmlElement(required=false)
	private InstanceConfigurations instances;
	@XmlElement(required=false)
	private Variables vars;
	
	@XmlAttribute
	private String productName;
	@XmlAttribute
	private String projectName;
	@XmlElement(required=true,name="goals")
	private BuildGoalsConfig goalsConfig;
	@XmlElement(required=false,name="templates")
	private BuildTemplatesConfig templatesConfig;
	
	@XmlElement(required=false,name="logging")
	private LogConfig logConfig;
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public InstanceConfigurations getInstances() {
		return instances;
	}

	public void setInstances(InstanceConfigurations instances) {
		this.instances = instances;
	}

	

	public BuildGoalsConfig getGoalsConfig() {
		return goalsConfig;
	}

	public void setGoalsConfig(BuildGoalsConfig goalsConfig) {
		this.goalsConfig = goalsConfig;
	}

	public LogConfig getLogConfig() {
		return logConfig;
	}

	public void setLogConfig(LogConfig logConfig) {
		this.logConfig = logConfig;
	}

	public Variables getVars() {
		return vars;
	}

	public void setVars(Variables vars) {
		this.vars = vars;
	}

	public BuildTemplatesConfig getTemplatesConfig() {
		return templatesConfig;
	}

	public void setTemplatesConfig(BuildTemplatesConfig templatesConfig) {
		this.templatesConfig = templatesConfig;
	}

	


}//end ProjectConfig