package net.unibld.core.task.impl.util;

import net.unibld.core.BuildTask;
import net.unibld.core.task.annotations.Task;

/**
 * Deletes a directory and its contents or multiple directories and their
 * contents if they exist. 
 * @author andor
 *
 */
@Task(name="clean",runnerClass=CleanTaskRunner.class)
public class CleanTask extends BuildTask {
	
	private static final long serialVersionUID = -159534448176121069L;
	private String failOnNotFound;
	private String dir;
	private String complete;
	private String dirs;
	
	
	/**
	 * @return Single directory to clean
	 */
	public String getDir() {
		return dir;
	}
	/**
	 * @param path Single directory to clean
	 */
	public void setDir(String path) {
		this.dir = path;
	}
	
	/**
	 * @return Multiple directories to clean, separated by ;
	 */
	public String getDirs() {
		return dirs;
	}
	/**
	 * @param dirs Multiple directories to clean, separated by ;
	 */
	public void setDirs(String dirs) {
		this.dirs = dirs;
	}
	/**
	 * @return "true" if the build directory (specified by the context attribute 
	 * build.dir) should also be cleaned
	 */
	public String getComplete() {
		return complete;
	}
	/**
	 * @param complete "true" if the build directory (specified by the context attribute 
	 * build.dir) should also be cleaned
	 */
	public void setComplete(String complete) {
		this.complete = complete;
	}
	/**
	 * @return "true" if the task should fail if the directory or any of the directories
	 * are not found
	 */
	public String getFailOnNotFound() {
		return failOnNotFound;
	}
	
	/**
	 * @param failOnNotFound "true" if the task should fail if the directory or any of the directories
	 * are not found
	 */
	public void setFailOnNotFound(String failOnNotFound) {
		this.failOnNotFound = failOnNotFound;
	}

	/**
	 * @return True if the build directory (specified by the context attribute 
	 * build.dir) should also be cleaned 
	 */
	public boolean isComplete() {
		return "true".equals(complete);
	}
	/**
	 * @return True if the task should fail if the directory or any of the directories
	 * are not found
	 */
	public boolean isFailOnNotFound() {
		return "true".equals(failOnNotFound);
	}
}
