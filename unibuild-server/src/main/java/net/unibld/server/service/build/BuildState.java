package net.unibld.server.service.build;

import java.io.Serializable;

import net.unibld.core.persistence.model.Build;

/**
 * A class that represents a build's current state and is cacheable for display on the website
 * @author andor
 *
 */
public class BuildState implements Serializable {
	
	private static final long serialVersionUID = -5064129921861772796L;
	
	private String buildId;
	private int progress;
	private boolean running;
	private boolean completed;
	
	private int currentTaskIdx;
	
	private String failedTaskClassName;
	private Integer failedTaskIdx;
	private Exception failureCause;
	private String errorLogPath;
	
	
	/**
	 * @return {@link Build} ID
	 */
	public String getBuildId() {
		return buildId;
	}
	/**
	 * @param buildId {@link Build} ID
	 */
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
	/**
	 * @return Progress 0-100
	 */
	public int getProgress() {
		return progress;
	}
	/**
	 * @param progress Progress 0-100
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}
	/**
	 * @return True if the build is running
	 */
	public boolean isRunning() {
		return running;
	}
	/**
	 * @param running True if the build is running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	/**
	 * @return Class name of the task failed or null if none failed yet
	 */
	public String getFailedTaskClassName() {
		return failedTaskClassName;
	}
	/**
	 * @param failedTaskClassName Class name of the task failed or null if none failed yet
	 */
	public void setFailedTaskClassName(String failedTaskClassName) {
		this.failedTaskClassName = failedTaskClassName;
	}
	/**
	 * @return Index of the task failed or null if none failed yet
	 */
	public Integer getFailedTaskIdx() {
		return failedTaskIdx;
	}
	/**
	 * @param failedTaskIdx Index of the task failed or null if none failed yet
	 */
	public void setFailedTaskIdx(Integer failedTaskIdx) {
		this.failedTaskIdx = failedTaskIdx;
	}
	/**
	 * @return Failure cause or null
	 */
	public Exception getFailureCause() {
		return failureCause;
	}
	/**
	 * @param failureCause Failure cause or null
	 */
	public void setFailureCause(Exception failureCause) {
		this.failureCause = failureCause;
	}
	/**
	 * @return True if successfully completed
	 */
	public boolean isCompleted() {
		return completed;
	}
	/**
	 * @param completed True if successfully completed
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	/**
	 * @return Error log path or null
	 */
	public String getErrorLogPath() {
		return errorLogPath;
	}
	/**
	 * @param errorLogPath Error log path or null
	 */
	public void setErrorLogPath(String errorLogPath) {
		this.errorLogPath = errorLogPath;
	}

	
	/**
	 * @return Index of the currently running task
	 */
	public int getCurrentTaskIdx() {
		return currentTaskIdx;
	}

	/**
	 * @param currentTaskIdx Index of the currently running task
	 */
	public void setCurrentTaskIdx(int currentTaskIdx) {
		this.currentTaskIdx = currentTaskIdx;
	}
	
}
