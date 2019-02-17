package net.unibld.core;
import java.util.List;

import net.unibld.core.config.ProjectConfig;

/**
 * A class that represents a project to build. The tool can handle one project at
 * a time.
 * 
 * A project specifies a ProjectConfig member that describes its properties and an
 * ordered list of BuildTasks that represent the building process of the project.
 * 
 * Project data should be initialized by a configuration file using the reader
 * method of the IConfigurationReader interface. Project configuration files
 * should be editable by the user either manually or a using a GUI.
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:16
 */
public class BuildProject {

	/**
	 * The list of tasks to execute during the build.
	 */
	private List<BuildTask> tasks;
	private ProjectConfig projectConfig;
	private String name;
	private String id;
	private String goal;
	
	

	/**
	 * @return List of underlying {@link BuildTask}s to execute
	 */
	public List<BuildTask> getTasks() {
		return tasks;
	}

	/**
	 * @param tasks List of {@link BuildTask}s to execute
	 */
	public void setTasks(List<BuildTask> tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return Project configuration
	 */
	public ProjectConfig getProjectConfig() {
		return projectConfig;
	}

	/**
	 * @param projectConfig Project configuration
	 */
	public void setProjectConfig(ProjectConfig projectConfig) {
		this.projectConfig = projectConfig;
	}

	/**
	 * @return Name of the project
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Name of the project
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Unique ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id Unique ID
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return Selected goal to run
	 */
	public String getGoal() {
		return goal;
	}


	/**
	 * @param goal Selected goal to run
	 */
	public void setGoal(String goal) {
		this.goal = goal;
	}
}//end BuildProject