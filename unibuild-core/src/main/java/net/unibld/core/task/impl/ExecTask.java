package net.unibld.core.task.impl;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;


/**
 * A task that executes a native executable on the file system or an O/S command.
 * @author andor
 * @version 1.0
 * @created 21-05-2013 17:16:29
 */
@Task(name="exec",runnerClass=ExecTaskRunner.class)
public class ExecTask extends BuildTask {

	
	private static final long serialVersionUID = 2820745452290303103L;
	private String path;
	private String args;
	
	private String logFile;
	private boolean suppressLoggingScheme;
	private boolean failOnError=true;
	
	private String baseDirectory;
	
	
	/**
	 * @return True if logging of this task suppresses the active logging scheme of the current build.
	 */
	public boolean isSuppressLoggingScheme() {
		return suppressLoggingScheme;
	}



	/**
	 * @param suppressLoggingScheme True if logging of this task suppresses the active logging scheme of the current build.
	 */
	public void setSuppressLoggingScheme(boolean suppressLoggingScheme) {
		this.suppressLoggingScheme = suppressLoggingScheme;
	}




	/**
	 * @return Path of the executable
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path Path of the executable
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return Program arguments
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * @param args Program arguments
	 */
	public void setArgs(String args) {
		this.args = args;
	}
	/**
	 * @return Log file to log to during execution
	 */
	public String getLogFile() {
		return logFile;
	}

	/**
	 * @param logFile Log file to log to during execution
	 */
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}



	/**
	 * @return True if the whole run should fail, if an error occurs during execution.
	 */
	public boolean isFailOnError() {
		return failOnError;
	}



	/**
	 * @param failOnError True if the whole run should fail, if an error occurs during execution.
	 */
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	/**
	 * @return The directory in which the program should be executed
	 */
	public String getBaseDirectory() {
		return baseDirectory;
	}

	/**
	 * @param baseDirectory The directory in which the program should be executed
	 */
	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}
}