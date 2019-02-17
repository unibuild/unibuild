package net.unibld.core.lib;

import java.io.File;
import java.io.IOException;

import net.unibld.core.build.BuildException;
import net.unibld.core.exec.CmdContext;
import net.unibld.core.exec.CmdExecutor;
import net.unibld.core.exec.IExecutionListener;
import net.unibld.core.log.LoggingScheme;
import net.unibld.core.task.ExecutionResult;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WinServiceManager implements IExecutionListener{
	enum Mode {
		CHECK_INSTALLED,
		INSTALL,
		UNINSTALL
	}
	private static final Logger LOG = LoggerFactory.getLogger(WinServiceManager.class);
	private String logFile;
	private boolean suppressLoggingScheme;
	private LoggingScheme logging;
	private Mode mode=null;
	private String query;
	private String installedServices;
	
	public boolean isServiceInstalled(CmdExecutor cmdExec,String name) {
		mode=Mode.CHECK_INSTALLED;
		query=name;
		
		try {
			CmdContext ctx=new CmdContext();
			ctx.setFailOnError(true);
			ctx.setListener(this);
			ExecutionResult result = cmdExec.execCmd(ctx,"sc queryex type= service state= all | find \"vds\"");
			if (result.isSuccess()&&installedServices!=null) {
				String[] split = installedServices.split("\\n");
				for (String line:split) {
					
				}
				return false;
			} else {
				LOG.error("Failed to check service installed: "+name);
				throw new BuildException("Failed to check service installed: "+name);
			}
		} catch (Exception e) {
			LOG.error("Failed to check service installed: "+name,e);
			throw new BuildException("Failed to check service installed: "+name,e);
		} 
	}
	
	public void logExecution(String line,String output) {
		if (logFile == null
				|| !isSuppressLoggingScheme()) {
			if (logging!=null) {
				logging.logDebug("Executing {0}...\n\n{1}\n", line, output);
			}
		}
		if (logFile != null) {
			File file = new File(logFile);
			file.getParentFile().mkdirs();
			try {
				FileUtils.writeStringToFile(file, output, "UTF-8");
			} catch (IOException e) {
				LOG.error("Failed to write output to file",e);
			}

		}
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public boolean isSuppressLoggingScheme() {
		return suppressLoggingScheme;
	}

	public void setSuppressLoggingScheme(boolean suppressLoggingScheme) {
		this.suppressLoggingScheme = suppressLoggingScheme;
	}

	public LoggingScheme getLogging() {
		return logging;
	}

	public void setLogging(LoggingScheme logging) {
		this.logging = logging;
	}
	public void handleOutput(String out) {
		if (mode==Mode.CHECK_INSTALLED) {
			installedServices=out;
		}
	}
}
