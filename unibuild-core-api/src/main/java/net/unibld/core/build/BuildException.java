package net.unibld.core.build;

/**
 * A runtime exception that signals a build error
 * @author andor
 *
 */
public class BuildException extends RuntimeException {
	
	private static final long serialVersionUID = -4681789456823203239L;

	private String logFilePath;
	/**
	 * Constructor with error message
	 * @param msg Error message
	 */
	public BuildException(String msg) {
		super(msg);
	}
	/**
	 * Constructor with error message and cause
	 * @param msg Error message 
	 * @param t Cause
	 */
	public BuildException(String msg,Throwable t) {
		super(msg,t);
	}
	/**
	 * Constructor with error message, cause and log file path
	 * @param msg Error message 
	 * @param exception Cause
	 * @param logpath Log file path
	 */
	public BuildException(String msg, Throwable exception, String logpath) {
		this(msg,exception);
		this.logFilePath=logpath;
	}
	/**
	 * @return Log file path
	 */
	public String getLogFilePath() {
		return logFilePath;
	}
	/**
	 * @param logFilePath Log file path
	 */
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}
}
