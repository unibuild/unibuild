package net.unibld.core.config;

import java.io.Serializable;

import net.unibld.core.persistence.model.ProjectAccessLevel;

/**
 * Configuration of a build goal.
 * @author andor
 *
 */
public class BuildGoalConfig implements Serializable {
	
	private static final long serialVersionUID = -7869005069698227006L;

	private TaskConfigurations tasks;

	private String name;
	private ProjectAccessLevel accessLevel;
	private boolean confirm;

		

	/**
	 * @return Name of the goal
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Name of the goal
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return Goal task configurations
	 */
	public TaskConfigurations getTasks() {
		return tasks;
	}

	/**
	 * @param tasks Goal task configurations
	 */
	public void setTasks(TaskConfigurations tasks) {
		this.tasks = tasks;
	}



	/**
	 * @return True if the goal needs user confirmation
	 */
	public boolean isConfirm() {
		return confirm;
	}

	/**
	 * @param confirm True if the goal needs user confirmation
	 */
	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	/**
	 * @return Access level necessary to run the goal
	 */
	public ProjectAccessLevel getAccessLevel() {
		return accessLevel;
	}

	/**
	 * @param accessLevel Access level necessary to run the goal
	 */
	public void setAccessLevel(ProjectAccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

	
}
