package net.unibld.core.task.impl.java.ant;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ThirdPartyExecTask;


/**
 * A task that executes an Ant task externally
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:27
 */
@Task(name="ant",runnerClass=AntTaskRunner.class)
public class AntTask extends ThirdPartyExecTask {

	
	private static final long serialVersionUID = -2468398654204732141L;
	
	private String buildFile;
	private String javaHome;
	private String target;
	private String lib;
	

	
	/**
	 * @return Path of the build file (-buildfile) (optional)
	 */
	public String getBuildFile() {
		return buildFile;
	}

	/**
	 * @param buildFile Path of the build file (-buildfile) (optional)
	 */
	public void setBuildFile(String buildFile) {
		this.buildFile = buildFile;
	}

	/**
	 * @return JAVA_HOME environment variable for Ant
	 */
	public String getJavaHome() {
		return javaHome;
	}

	/**
	 * @param javaHome JAVA_HOME environment variable for Ant
	 */
	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	

	/**
	 * @return Target name to execute (optional)
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target Target name to execute (optional)
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return Library folder that contains necessary jars (-lib /home/ant/extras) or a semi-colon separated 
	 * list of jars on the Ant classpath (-lib one.jar;another.jar) (optional)
	 */
	public String getLib() {
		return lib;
	}
	/**
	 * @param lib Library folder that contains necessary jars (-lib /home/ant/extras) or a semi-colon separated 
	 * list of jars on the Ant classpath (-lib one.jar;another.jar) (optional)
	 */
	public void setLib(String lib) {
		this.lib = lib;
	}


}//end AntTask