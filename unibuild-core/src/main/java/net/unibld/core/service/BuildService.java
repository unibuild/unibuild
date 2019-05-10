package net.unibld.core.service;

import java.util.List;

import net.unibld.core.BuildProject;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.BuildTaskResult;

/**
 * {@link Build} persistence interface
 * 
 */
public interface BuildService {
	/**
	 * Records the start of a build in the persistent store
	 * @param buildId GUID of the build
	 * @param project Project 
	 * @param path Project path
	 * @param goal Goal executed
	 * @param user Current user
	 * @return The {@link Build} started
	 */
	public Build startBuild(String buildId,BuildProject project,String path,String goal,String user);
	/**
	 * Records the successful completion of a build in the persistent store
	 * @param buildId Id of the {@link Build} record
	 */
	public void buildCompleted(String buildId);
	/**
	 * Records the failure of a build in the persistent store
	 * @param buildId Id of the {@link Build} record
	 * @param taskClass Class of the task that failed
	 * @param taskIdx Index of the task that failed
	 * @param t Error thrown
	 * @param logFilePath Log file path
	 */
	public void buildFailed(String buildId,String taskResultId,String taskClass,int taskIdx,Throwable t,String logFilePath);
	/**
	 * Finds a {@link Build} record by its string id
	 * @param id Build record id
	 * @return Build record or null
	 */
	public Build findBuild(String id);
	/**
	 * Deletes all build records
	 */
	public void deleteAllBuilds();
	/**
	 * Requests the cancellation of a specific build
	 * @param buildId Build id
	 */
	public void cancelBuild(String buildId);
	public boolean isBuildCancelled(String id);
	public BuildTaskResult taskStarted(String buildId, String className, String taskName, int taskIdx);
	public BuildTaskResult taskCompleted(String taskResultId);
	public List<BuildTaskResult> getBuildTaskResults(Build build);
}
