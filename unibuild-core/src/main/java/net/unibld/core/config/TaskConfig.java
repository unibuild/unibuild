package net.unibld.core.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A configuration class that describes a specific BuildTask using a TaskContext.
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "taskConfig")
public class TaskConfig implements Serializable, Cloneable {
	@XmlElement
	private TaskContext taskContext;
	@XmlAttribute
	private String taskType;

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public TaskConfig getClone() throws CloneNotSupportedException {
		TaskConfig cfg = (TaskConfig) clone();
		if (this.taskContext!=null) {
			cfg.taskContext = this.taskContext.getClone();
		}
		return cfg;
	}

}//end TaskConfig