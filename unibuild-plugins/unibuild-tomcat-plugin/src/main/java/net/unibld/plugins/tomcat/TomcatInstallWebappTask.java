package net.unibld.plugins.tomcat;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.server.ServerTask;

/**
 * Installs a web application WAR file to an Apache Tomcat server instance
 * by copying the WAR file to its webapps folder.
 * @author andor
 *
 */
@Task(name="tomcat-install-webapp",runnerClass=TomcatInstallWebappTaskRunner.class)
public class TomcatInstallWebappTask extends ServerTask {
	
	private static final long serialVersionUID = -5454114610189335414L;
	private boolean clean;
	private String war;
	private String targetWarName;
	/**
	 * @return True if work directory should be cleaned
	 */
	public boolean isClean() {
		return clean;
	}
	/**
	 * @param clean True if work directory should be cleaned
	 */
	public void setClean(boolean clean) {
		this.clean = clean;
	}
	/**
	 * @return WAR file path to install
	 */
	public String getWar() {
		return war;
	}
	/**
	 * @param war WAR file path to install
	 */
	public void setWar(String war) {
		this.war = war;
	}
	/**
	 * @return Target WAR file name
	 */
	public String getTargetWarName() {
		return targetWarName;
	}
	/**
	 * @param target Target WAR file name
	 */
	public void setTargetWarName(String target) {
		this.targetWarName = target;
	}
	
}
