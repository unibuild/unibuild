package net.unibld.core.util.version;

import net.unibld.core.config.ProjectConfig;
import net.unibld.core.task.ITaskLogger;

public class VersionContext {
	private ProjectConfig projectConfig;
	private String pomFile;
	
	private boolean buildNum;
	private boolean pom;
	private ITaskLogger taskLogger;
	
	private String buildNumberProperties;
	private String buildNumberPropertyName;
	
	
	public ProjectConfig getProjectConfig() {
		return projectConfig;
	}
	public void setProjectConfig(ProjectConfig projectConfig) {
		this.projectConfig = projectConfig;
	}
	public String getPomFile() {
		return pomFile;
	}
	public void setPomFile(String pomFile) {
		this.pomFile = pomFile;
	}
	
	public boolean isBuildNum() {
		return buildNum;
	}
	public void setBuildNum(boolean buildNum) {
		this.buildNum = buildNum;
	}
	public boolean isPom() {
		return pom;
	}
	public void setPom(boolean pom) {
		this.pom = pom;
	}
	public ITaskLogger getTaskLogger() {
		return taskLogger;
	}
	public void setTaskLogger(ITaskLogger taskLogger) {
		this.taskLogger = taskLogger;
	}
	public String getBuildNumberProperties() {
		return buildNumberProperties;
	}
	public void setBuildNumberProperties(String buildNumberProperties) {
		this.buildNumberProperties = buildNumberProperties;
	}
	public String getBuildNumberPropertyName() {
		return buildNumberPropertyName;
	}
	public void setBuildNumberPropertyName(String buildNumberPropertyName) {
		this.buildNumberPropertyName = buildNumberPropertyName;
	}
}
