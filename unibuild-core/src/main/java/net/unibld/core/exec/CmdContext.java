package net.unibld.core.exec;

import java.io.File;
import java.util.Map;

/**
 * Context class for execution of a command line process
 * @author andor
 *
 */
public class CmdContext {
	private String output;
	private String logFilePath;
	private File workingDir;

	private boolean failOnError;
	private IExecutionListener listener;
	private Map env;
	
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getLogFilePath() {
		return logFilePath;
	}
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}
	public File getWorkingDir() {
		return workingDir;
	}
	public void setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
	}
	public boolean isFailOnError() {
		return failOnError;
	}
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}
	public IExecutionListener getListener() {
		return listener;
	}
	public void setListener(IExecutionListener listener) {
		this.listener = listener;
	}
	public Map getEnv() {
		return env;
	}
	public void setEnv(Map env) {
		this.env = env;
	}
}
