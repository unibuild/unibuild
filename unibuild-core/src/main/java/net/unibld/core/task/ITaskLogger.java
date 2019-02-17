package net.unibld.core.task;

public interface ITaskLogger {
	void logDebug(String msg);
	void logInfo(String msg);
	void logError(String msg);
	
	void logError(String msg,Exception ex);
	void logTaskErrorLogPath(String name,String path);
	void logTask(String msg,String...args);
	
}
