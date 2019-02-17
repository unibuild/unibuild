package net.unibld.core.build;

import net.unibld.core.persistence.model.Build;

/**
 * A listener to provide callbacks for a build process
 * @author andor
 *
 */
public interface BuildEventListener {

	/**
	 * Build completed event
	 * @param buildId GUID of the build
	 */
	void buildCompleted(String buildId);
	/**
	 * Build failed event
	 * @param buildId GUID of the build
	 * @param taskClassName Class name of the task that failed
	 * @param taskIdx Index of the task that failed
	 * @param ex Exception caught
	 */
	void buildFailed(String buildId, String taskClassName, int taskIdx, Exception ex);

	/**
	 * Error log created to a log file
	 * @param buildId GUID of the build
	 * @param taskClassName Class name of the task that failed
	 * @param logFilePath Log file created
	 */
	void errorLogCreated(String buildId,String taskClassName, String logFilePath);
	/**
	 * Build started event
	 * @param b {@link Build} entity
	 */
	void buildStarted(Build b);

}
