package net.unibld.plugins.tomcat;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.server.ServerTask;

/**
 * A task related to the management of Apache Tomcat servers.
 * @author andor
 *
 */
@Task(name="tomcat",runnerClass=TomcatTaskRunner.class)
public class TomcatTask extends ServerTask {
	
	private static final long serialVersionUID = 4322531926895178318L;
	private String exportCatalinaHome;
	private String javaHome;

	/**
	 * @return True if Tomcat path should be exported to the CATALINA_HOME
	 * environment variable prior to execution
	 */
	public boolean isExportCatalinaHome() {
		return "true".equals(exportCatalinaHome);
	}
	

	/**
	 * @param exportCatalinaHome "true" if Tomcat path should be exported to the CATALINA_HOME
	 * environment variable prior to execution
	 */
	public void setExportCatalinaHome(String exportCatalinaHome) {
		this.exportCatalinaHome = exportCatalinaHome;
	}

	/**
	 * @return Java home used by the server, and exported into the JAVA_HOME env var
	 */
	public String getJavaHome() {
		return javaHome;
	}

	/**
	 * @param javaHome Java home used by the server, and exported into the JAVA_HOME env var
	 */
	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	

	/**
	 * @return Returns "true" if Tomcat path should be exported to the CATALINA_HOME
	 * environment variable prior to execution
	 */
	public String getExportCatalinaHome() {
		return exportCatalinaHome;
	}
}
