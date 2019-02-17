package net.unibld.core.service;

import java.util.List;

import net.unibld.core.BuildProject;
import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;

/**
 * {@link Project} persistence interface
 * @author andor
 *
 */
public interface ProjectService {
	/**
	 * @return Projects available locally
	 */
	List<Project> getAvailableProjects();
	/**
	 * @return Available projects, ordered by their names ascending
	 */
	List<Project> getAvailableProjectsOrderByName();
	/**
	 * @param id Project id (string)
	 * @return Project by id or null
	 */
	Project findProject(String id);
	/**
	 * Saves a new or an existing project
	 * @param p Project to save as {@link BuildProject}
	 * @param path Project XML path
	 * @param user Current user (typically the logged on OS user)
	 * @return Project record saved
	 */
	Project saveProject(BuildProject p,String path,String user);
	/**
	 * @return The project used last or null
	 */
	Project getLastProject();
	/**
	 * Deletes all {@link Project} records
	 */
	void deleteAllProjects();
	/**
	 * Returns the goal last run, of any projects
	 * @return The last run goal of any projects
	 */
	String getLastGoal();
	/**
	 * Returns a {@link Project} record identified by its name
	 * @param projectName Project name
	 * @return Project record found or null
	 */
	Project getProjectByName(String projectName);
	/**
	 * Returns a {@link Project} record identified by its project file path
	 * @param path Project file path
	 * @return Project record found or null
	 */
	Project getProjectByPath(String path);
	/**
	 * Deletes the specified {@link Project} record and its related {@link Build} runs.
	 * @param project Project to delete
	 */
	void deleteProject(Project project);
}
