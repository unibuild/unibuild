package net.unibld.core.build;

import java.util.ArrayList;
import java.util.List;

import net.unibld.core.BuildProject;
import net.unibld.core.log.Verbosity;

/**
 * A class that represents a request to start a build process (goal).
 * @author andor
 *
 */
public class BuildRequest {
	private String buildId;
	private BuildProject project;
	private String goal;
	private Verbosity verbosity;
	private String userId;
	
	private List<ProgressListener> progressListeners=new ArrayList<>();
	private List<BuildEventListener> buildEventListeners=new ArrayList<>();
	
	/**
	 * Adds a build event listener to the build request
	 * @param l Listener to add
	 */
	public void addBuildEventListener(BuildEventListener l) {
		buildEventListeners.add(l);
	}
	
	/**
	 * Adds a progress listener to the builder
	 * @param id Build ID
	 * @param l Listener to add
	 */
	public void addProgressListener(ProgressListener l) {
		progressListeners.add(l);
	}
	
	/**
	 * @return UUID of the build run
	 */
	public String getBuildId() {
		return buildId;
	}
	/**
	 * @param buildId UUID of the build run
	 */
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
	/**
	 * @return Project to build
	 */
	public BuildProject getProject() {
		return project;
	}
	/**
	 * @param project Project to build
	 */
	public void setProject(BuildProject project) {
		this.project = project;
	}
	/**
	 * @return Goal to execute
	 */
	public String getGoal() {
		return goal;
	}
	/**
	 * @param goal Goal to execute
	 */
	public void setGoal(String goal) {
		this.goal = goal;
	}
	/**
	 * @return Verbosity config
	 */
	public Verbosity getVerbosity() {
		return verbosity;
	}
	/**
	 * @param verbosity Verbosity config
	 */
	public void setVerbosity(Verbosity verbosity) {
		this.verbosity = verbosity;
	}
	/**
	 * @return User ID (if using the CLI version, it should be the OS user, if using the web version, it 
	 * should be the logged on web user)
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId User ID (if using the CLI version, it should be the OS user, if using the web version, it 
	 * should be the logged on web user)
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return List of registered progress listeners
	 */
	public List<ProgressListener> getProgressListeners() {
		return progressListeners;
	}
	/**
	 * @return List of registered build event listeners
	 */
	public List<BuildEventListener> getBuildEventListeners() {
		return buildEventListeners;
	}
}
