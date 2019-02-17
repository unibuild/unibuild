package net.unibld.core.task.impl.java.buildnumber;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;

/**
 * Increases the build number in a Java/Maven generated buildNumber.properties file
 * @author andor
 *
 */
@Task(name="buildnumber-inc",runnerClass=BuildNumberIncreaseTaskRunner.class)
public class BuildNumberIncreaseTask extends BuildTask {
	
	private static final long serialVersionUID = 5289959021018458860L;
	private String buildNumberProperties;
	private String buildNumberPropertyName;
	
	/**
	 * @return Build number properties file
	 */
	public String getBuildNumberProperties() {
		return buildNumberProperties;
	}
	/**
	 * @param buildNumberProperties Build number properties file
	 */
	public void setBuildNumberProperties(String buildNumberProperties) {
		this.buildNumberProperties = buildNumberProperties;
	}
	/**
	 * @return Build number property name
	 */
	public String getBuildNumberPropertyName() {
		return buildNumberPropertyName;
	}
	/**
	 * @param buildNumberPropertyName Build number property name
	 */
	public void setBuildNumberPropertyName(String buildNumberPropertyName) {
		this.buildNumberPropertyName = buildNumberPropertyName;
	}
}
