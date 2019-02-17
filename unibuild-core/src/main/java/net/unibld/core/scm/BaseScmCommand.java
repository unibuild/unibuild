package net.unibld.core.scm;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.security.BuildCredential;
import net.unibld.core.task.impl.ExecTask;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

public abstract class BaseScmCommand<T extends ExecTask> implements IScmCommand<T> {
	protected String userName;
	protected BuildCredential credential;
	
	private String logFilePath;
	protected String origPath;
	protected String buildDir;
	
	protected boolean failOnError;
	protected Map environment;
	
	protected File workingDirectory;

	protected CmdExecutor executor;
	
	
	
	private static final DateFormat FILE_DF=new SimpleDateFormat("yyyyMMdd_HHmmss");
	protected String getLogFileName() {
		return String.format("%s_%s.log",getExecutable(),FILE_DF.format(new Date()));
	}
	
	public String getOrigPath() {
		return origPath;
	}
	public void setOrigPath(String origPath) {
		this.origPath = origPath;
	}
	
	public String getBuildDir() {
		return buildDir;
	}
	public void setBuildDir(String buildDir) {
		this.buildDir = buildDir;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setLogFilePath(String logFilePath) {
		this.logFilePath=logFilePath;
	}
	public String getLogFilePath() {
		return logFilePath;
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public Map getEnvironment() {
		return environment;
	}

	public void setEnvironment(Map environment) {
		this.environment = environment;
	}

	public BuildCredential getCredential() {
		return credential;
	}

	public void setCredential(BuildCredential credential) {
		this.credential = credential;
	}
	

	public File getWorkingDirectory() {
		return workingDirectory;
	}
	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public CmdExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(CmdExecutor executor) {
		this.executor = executor;
	}
	

}
