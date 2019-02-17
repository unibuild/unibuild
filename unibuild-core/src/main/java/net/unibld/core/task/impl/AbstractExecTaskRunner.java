package net.unibld.core.task.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.exec.IExecutionListener;
import net.unibld.core.task.BaseTaskRunner;
import net.unibld.core.task.ExecutionResult;
import net.unibld.core.util.PlatformHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for task runners that execute another external process using 
 * Commons Exec from within Java.
 * @author andor
 * @version 1.0
 * @param <T> Generic task type that should extend {@link ExecTask}
 * @created 21-05-2013 17:16:29
 */
public abstract class AbstractExecTaskRunner<T extends ExecTask> extends BaseTaskRunner<T> {
	protected class ExecutionListenerImpl implements IExecutionListener {
		private T task;
		protected ExecutionListenerImpl(T task) {
			this.task=task;
		}
		public void logExecution(String line,String output) {
			if (task.getLogFile() == null
					|| !task.isSuppressLoggingScheme()) {
				getLogging().logDebug("Executing {0}...\n\n{1}\n", line, output);
			}
			if (task.getLogFile() != null) {
				File file = new File(task.getLogFile());
				file.getParentFile().mkdirs();
				try {
					FileUtils.writeStringToFile(file, output, "UTF-8");
				} catch (IOException e) {
					LoggerFactory.getLogger(getClass()).error("Failed to write output to file",e);
				}

			}
		}
		@Override
		public void handleOutput(String output) {
			// TODO Auto-generated method stub
			
		}
	}

	@Autowired
	protected CmdExecutor cmdExecutor;

	
	

	@SuppressWarnings("rawtypes")
	protected Map getEnvironment(T task) {
		return null;
	}
	protected ExecutionResult execute(T task) {
		return execCmd(task);
	}

	protected ExecutionResult execCmd(T task) {
		validate(task);
		
		
		String args = getArguments(task);
		String line=null;
		if (args!=null) {
			line = String.format("%s %s",task.getPath() , args);
		} else {
			line = task.getPath();
		}
		
		String logFilePath = getLogFilePath(task);
		
		try {
			CmdContext ctx=new CmdContext();
			if (task.getBaseDirectory()!=null) {
				File workingDir=new File(task.getBaseDirectory());
				if (workingDir.exists()&&workingDir.isDirectory()) {
					ctx.setWorkingDir(workingDir);
				} else {
					throw new IllegalStateException("Base directory does not exist: "+workingDir.getAbsolutePath());
				}
			}
			ctx.setLogFilePath(logFilePath);
			ctx.setEnv(getEnvironment(task));
			ctx.setFailOnError(isFailOnError(task));
			ctx.setListener(createListener(task));
			ExecutionResult ret = cmdExecutor.execCmd(ctx,line);
			ret.setLogFilePath(ctx.getLogFilePath());
			return ret;
		} catch (Exception ex) {
			String msg = String.format("Failed to execute process %s",task.getPath());
			logError(msg, ex);
			if (isFailOnError(task)) {
				
				return ExecutionResult.buildError(msg, ex,logFilePath);
			} else {
				return ExecutionResult.buildSuccess();
			}
		}
	}

	
	

	

	protected IExecutionListener createListener(T task) {
		return new ExecutionListenerImpl(task);
	}
	protected String getArguments(T task) {
		return task.getArgs();
	}

	
	


	

	protected void validate(T task) {
		if (task==null) {
			throw new IllegalStateException("Task was null");
		}
		if (task.getTaskConfig()==null) {
			throw new IllegalStateException("Task config was null");
		}
		if (task.getTaskConfig().getTaskContext()==null) {
			throw new IllegalStateException("Task context was null");
		}
		
		
		
		if (task.getPath()==null) {
			throw new IllegalStateException("Task path was null");
		}

	}

	

	
	
	@Override
	protected Logger getLogger() {
		return LoggerFactory.getLogger(getClass());
	}


	/**
	 * @param task Task to check
	 * @return True if the task should fail if the executed process fails
	 */
	public boolean isFailOnError(T task) {
		return task.isFailOnError();
	}

	protected String escapeWindowsSpaces(String path) {
		if (!PlatformHelper.isWindows()) {
			return path;
		}
		
		if (path==null||!path.contains(" ")) {
			return path;
		}
		return String.format("\"%s\"",path);
	}
	
	
	
	private static final DateFormat FILE_DF=new SimpleDateFormat("yyyyMMdd_HHmmss");
	protected String getLogFileNameForTask(String taskName) {
		return String.format("%s_%s.log",taskName,FILE_DF.format(new Date()));
	}

	/**
	 * @return Path of the log file to use or null if not specified
	 */
	protected String getLogFilePath(T task) {
		String origPath=new File(getBuildDirPath(task)).getAbsolutePath();
		
		return FilenameUtils.concat(origPath, FilenameUtils.concat("logs",getLogFileNameForTask(getTaskName())));
	}
	
}//end ShellTaskRunner