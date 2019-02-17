package net.unibld.server.service.build;

/**
 * An interface for a queueing build runs.
 * @author andor
 *
 */
public interface BuildQueue {
	/**
	 * Adds a build run to the queue. If there is no build currently running, it automatically
	 * starts the first item in the queue, which in most cases will be the one just added.<br>
	 * Only one build can run at the same time and only one instance of the same goal in the same 
	 * project can be present in the queue at the same time.
	 * @param request Build queue request
	 * @return True if the build has been started immediately, false if it has been queued.
	 */
	public boolean addToQueue(BuildQueueRequest request);

	/**
	 * @return True if a build is running
	 */
	public boolean isRunning();
	/**
	 * @return True if a build is running or has been completed 
	 */
	public boolean isRunningOrCompleted();

	/**
	 * @return Display name of the currently running build or null if there is none.
	 */
	public String getCurrentOrLastBuildLabel();

	/**
	 * @return ID of the currently running build or null if there is none.
	 */
	public String getCurrentBuildId();
	
	/**
	 * @return ID of the last run build or null if there is none.
	 */
	public String getLastBuildId();
	/**
	/**
	 * @param id Unique build GUID
	 * @return True if the specified build is running
	 */
	public boolean isBuildRunning(String id);

	/**
	 * Checks if there is no current item running and the build queue is not empty, if so, starts the first build.
	 */
	public void checkForRun();

	/**
	 * @return The number of items waiting in the queue
	 */
	public int getWaitingQueueSize();

	/**
	 * @return Textual representation of the elements waiting in the queue
	 */
	public String getWaitingQueueContent();

	/**
	 * Cancels the current build.
	 */
	public void cancelCurrent();

	/**
	 * Cancels all builds in the queue. It does not cancel the current one, though.
	 */
	public void cancelWaiting();
}
