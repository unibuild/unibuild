package net.unibld.core.log;

import net.unibld.core.BuildProject;
import net.unibld.core.build.ParameterMap;
import net.unibld.core.build.TaskResult;
import net.unibld.core.config.LoggerConfig;

/**
 * Defines a common interface for build logging
 * @author andor
 * @version 1.0
 * @created 22-05-2013 3:47:24
 */
public interface IBuildLogger {

	public void logDebug(String msg,String... args);
	
	public void logTask(String taskName,String msg,String... args);
	public void logTaskResult(String taskName,TaskResult result);
	public void logTaskResult(String taskName, String result);
	public void logError(String msg,Throwable ex,String... args);
	public void logError(String msg,String... args);

	public void configure(LoggerConfig lc,boolean verbose,boolean trace);

	public void welcome(BuildProject project,String goal, String logFile);

	public void logTaskFailureReason(String taskName, String failureReason);
	public void logTaskErrorLogPath(String taskName, String logFilePath);
	void setBuildId(String buildId);
}//end IBuildLogger