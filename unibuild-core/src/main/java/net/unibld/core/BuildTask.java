package net.unibld.core;
import java.io.Serializable;

import net.unibld.core.config.TaskConfig;

/**
 * The basic unit in the build process that defines a single, integer task to execute.
 * @author andor
 */
public abstract class BuildTask implements Serializable,Cloneable {

	
	private static final long serialVersionUID = -7612134733434914560L;

	private TaskConfig taskConfig;

	protected String executeCondition;
	

	/**
	 * @return Configuration of the task
	 */
	public TaskConfig getTaskConfig() {
		return taskConfig;
	}

	/**
	 * @param taskConfig Configuration of the task
	 */
	public void setTaskConfig(TaskConfig taskConfig) {
		this.taskConfig = taskConfig;
	}

	/**
	 * @return An exectution condition in EL or null
	 */
	public String getExecuteCondition() {
		return executeCondition;
	}

	/**
	 * @param executeCondition An exectution condition in EL or null
	 */
	public void setExecuteCondition(String executeCondition) {
		this.executeCondition = executeCondition;
	}

	/**
	 * @return True if the task is executing unconditionally
	 */
	public boolean isExecuting() {
		return executeCondition==null||executeCondition.equals("true");
	}

	public BuildTask getClone() throws CloneNotSupportedException {
		return (BuildTask) clone();
	}

	
}