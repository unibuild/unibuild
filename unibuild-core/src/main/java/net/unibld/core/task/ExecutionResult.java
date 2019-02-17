package net.unibld.core.task;

import net.unibld.core.BuildTask;
import net.unibld.core.build.TaskResult;

/**
 * Result of the execution of a {@link BuildTask}
 * @author andor
 *
 */
public class ExecutionResult {
	private TaskResult result;
	private String message;
	private Throwable exception;
	private String logFilePath;
	
	/**
	 * @return Result of the task as an enum
	 */
	public TaskResult getResult() {
		return result;
	}
	/**
	 * @param result Result of the task as an enum
	 */
	public void setResult(TaskResult result) {
		this.result = result;
	}
	/**
	 * @return Message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message Message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return Exception if an error occurred
	 */
	public Throwable getException() {
		return exception;
	}
	/**
	 * @param exception Exception if an error occurred
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	/**
	 * @return True if the execution was successful ( {@link TaskResult}.OK)
	 */
	public boolean isSuccess() {
		return result!=null&&result==TaskResult.OK;
	}
	/**
	 * Creates a result for a successful execution
	 * @return Result object
	 */
	public static ExecutionResult buildSuccess() {
		ExecutionResult ret=new ExecutionResult();
		ret.result=TaskResult.OK;
		return ret;
	}
	/**
	 * Creates a result for a failed execution
	 * @param msg Error message
	 * @param ex Cause
	 * @return Result object
	 */
	public static ExecutionResult buildError(String msg,Throwable ex) {
		ExecutionResult ret=new ExecutionResult();
		ret.result=TaskResult.FAILED;
		ret.message=msg;
		ret.exception=ex;
		return ret;
	}
	/**
	 * Creates a result for a failed execution
	 * @param msg Error message
	 * @return Result object
	 */
	public static ExecutionResult buildError(String msg) {
		ExecutionResult ret=new ExecutionResult();
		ret.result=TaskResult.FAILED;
		ret.message=msg;
		return ret;
	}
	public void setLogFilePath(String logFilePath) {
		this.logFilePath=logFilePath;
	}
	public static ExecutionResult buildError(String msg, Exception ex,
			String logPath) {
		ExecutionResult ret = buildError(msg, ex);
		ret.setLogFilePath(logPath);
		return ret;
	}
	public String getLogFilePath() {
		return logFilePath;
	}
}
