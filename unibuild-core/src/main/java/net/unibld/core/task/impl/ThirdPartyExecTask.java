package net.unibld.core.task.impl;

import net.unibld.core.config.InstallationType;

/**
 * Base class for tasks that execute a third-party program that can be installed, bundled, on the path or
 * just at a specified location.
 * @author andor
 *
 */
public abstract class ThirdPartyExecTask extends ExecTask {
	private InstallationType installationType=InstallationType.path;
	private String installationDir;
	private String properties;
	
	
	/**
	 * @return Installation type
	 */
	public InstallationType getInstallationType() {
		return installationType;
	}

	/**
	 * @param installationType Installation type
	 */
	public void setInstallationType(InstallationType installationType) {
		this.installationType = installationType;
	}

	/**
	 * @return Installation path
	 */
	public String getInstallationDir() {
		return installationDir;
	}

	/**
	 * @param installationDir Installation path
	 */
	public void setInstallationDir(String installationDir) {
		this.installationDir = installationDir;
	}
	
	

	/**
	 * @return JVM arguments in a [key]=[value];[key]=[value];... format
	 */
	public String getProperties() {
		return properties;
	}

	/**
	 * @param properties  JVM arguments in a [key]=[value];[key]=[value];... format
	 */
	public void setProperties(String properties) {
		this.properties = properties;
	}
}
