package net.unibld.core.task.impl.util;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;

@Task(name="version-file-create",runnerClass=VersionFileCreateTaskRunner.class)
public class VersionFileCreateTask extends BuildTask {
	private String version;
	
	private String pomFile;
	
	private String path;
	
	private String buildNumberProperties;
	private String buildNumberPropertyName;
	
	public String getPomFile() {
		return pomFile;
	}
	public void setPomFile(String pomFile) {
		this.pomFile = pomFile;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
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
