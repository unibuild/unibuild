package net.unibld.core.log;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import net.unibld.core.BuildProject;
import net.unibld.core.build.TaskResult;
import net.unibld.core.config.LoggerConfig;

@Component
public class LoggingScheme implements IBuildLogger {
	private List<IBuildLogger> loggers=new ArrayList<>();
	
	public void clearLoggers() {
		loggers.clear();
	}
	public void addLogger(IBuildLogger l) {
		loggers.add(l);
	}
	public void logDebug(String msg,String... args) {
		for (IBuildLogger l:loggers) {
			l.logDebug(msg,args);
		}
	}
	
	public void logTask(String taskName,String msg,String... args) {
		for (IBuildLogger l:loggers) {
			l.logTask(taskName,msg,args);
		}
	}
	public void logTaskResult(String taskName,TaskResult result) {
		for (IBuildLogger l:loggers) {
			l.logTaskResult(taskName,result);
		}
	}
	public void logTaskResult(String taskName,String result) {
		for (IBuildLogger l:loggers) {
			l.logTaskResult(taskName,result);
		}
	}
	public void logError(String msg,Throwable ex,String... args) {
		for (IBuildLogger l:loggers) {
			l.logError(msg,ex,args);
		}
	}
	public void logError(String msg,String... args) {
		for (IBuildLogger l:loggers) {
			l.logError(msg,args);
		}
	}

	

	public void welcome(BuildProject project,String goal,String logFile) {
		for (IBuildLogger l:loggers) {
			l.welcome(project,goal,logFile);
		}
	}

	public boolean containsFileLogging() {
		if (loggers==null||loggers.size()==0) {
			return false;
		}
		
		for (IBuildLogger l:loggers) {
			if (SimpleFileLogger.class.isAssignableFrom(l.getClass())) {
				return true;
			}
		}
		return false;
	}

	public String getLogFile() {
		if (loggers==null||loggers.size()==0) {
			return null;
		}
		
		for (IBuildLogger l:loggers) {
			if (SimpleFileLogger.class.isAssignableFrom(l.getClass())) {
				SimpleFileLogger fl=(SimpleFileLogger) l;
				return fl.getFilePath();
			}
		}
		return null;
	}

	public void logTaskFailureReason(String taskName, String failureReason) {
		for (IBuildLogger l:loggers) {
			l.logTaskFailureReason(taskName,failureReason);
		}
	}
	
	public void logTaskErrorLogPath(String taskName, String path) {
		for (IBuildLogger l:loggers) {
			l.logTaskErrorLogPath(taskName,path);
		}
	}
	public void addLoggers(List<IBuildLogger> loggers) {
		if (loggers==null) {
			return;
		}
		for (IBuildLogger l:loggers) {
			this.loggers.add(l);
		}
	}
	@Override
	public void configure(LoggerConfig lc, boolean verbose, boolean trace) {
		// empty
		
	}
	
	public void setBuildId(String buildId) {
		for (IBuildLogger l:loggers) {
			l.setBuildId(buildId);
		}
	}
}
