package net.unibld.server.web.jsf;

import java.util.Date;

import net.unibld.core.BuildProject;
import net.unibld.core.build.TaskResult;
import net.unibld.core.config.LoggerConfig;
import net.unibld.core.log.IBuildLogger;
import net.unibld.core.log.LogFormatter;
import net.unibld.core.log.WelcomeMessageHelper;
import net.unibld.server.entities.build.BuildLog;
import net.unibld.server.entities.log.LogLevel;
import net.unibld.server.service.build.LogConsumer;

public class DatatableLogger implements IBuildLogger {
	private boolean verbose;
	private LogConsumer consumer;
	private String buildId;
	
	public DatatableLogger(LogConsumer consumer) {
		this.consumer=consumer;
	}
	@Override
	public void logDebug(String msg, String... args) {
		if (isVerbose()) {
			createLog(
					"DEBUG:" + LogFormatter.replaceArgs(msg, args),LogLevel.DEBUG);
		}
	}

	private void createLog(String msg, LogLevel level) {
		if (buildId==null) {
			throw new IllegalStateException("Build ID was null");
		}
		BuildLog log=new BuildLog();
		log.setMessage(msg);
		log.setLevel(level);
		log.setDate(new Date());
		consumer.addLogMessage(buildId,log);
	}

	@Override
	public void logTask(String taskName, String msg, String... args) {
		String str = LogFormatter.replaceArgs(msg,args);
		createLog(str,LogLevel.INFO);
	}

	@Override
	public void logTaskResult(String taskName, TaskResult result) {
		if (buildId==null) {
			throw new IllegalStateException("Build ID was null");
		}
		String str=String.format(" %s", result.name());
		consumer.appendToLastLog(buildId,str);
	}

	@Override
	public void logTaskResult(String taskName, String result) {
		if (buildId==null) {
			throw new IllegalStateException("Build ID was null");
		}
		String str = String.format(" %s", result);
		consumer.appendToLastLog(buildId,str);
	}

	@Override
	public void logError(String msg, Throwable ex, String... args) {
		createLog("ERROR: "+LogFormatter.replaceArgs(msg,args),LogLevel.ERROR);
		
	}
 
	@Override
	public void logError(String msg, String... args) {
		createLog("ERROR: "+LogFormatter.replaceArgs(msg,args),LogLevel.ERROR);
	}

	@Override
	public void configure(LoggerConfig lc, boolean verbose, boolean trace) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void welcome(BuildProject project,String goal,String logFile) {
		createLog(WelcomeMessageHelper.getWelcomeMessage(project,goal,logFile),LogLevel.INFO);
	}

	@Override
	public void logTaskFailureReason(String taskName, String failureReason) {
		createLog(String.format("%s failure reason: %s", taskName,failureReason!=null?failureReason:"N/A"),LogLevel.ERROR);
	}
	
	@Override
	public void logTaskErrorLogPath(String taskName, String logFilePath) {
		createLog(String.format("%s error log: %s", taskName,logFilePath!=null?logFilePath:"N/A"),LogLevel.ERROR);
	}


	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public String getBuildId() {
		return buildId;
	}
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

}
