package net.unibld.core.config;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.unibld.core.BuildTask;

/**
 * Element that contains a list of dynamic task configurations that are transient from JAXB's point of view.
 * @author andor
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tasks")
public class TaskConfigurations implements Serializable {
	
	private static final long serialVersionUID = -1619731851183941453L;
	
	@XmlTransient
	private List<BuildTask> tasks;

	/**
	 * @return Dynamic task configurations
	 */
	public List<BuildTask> getTasks() {
		return tasks;
	}

	/**
	 * @param tasks Dynamic task configurations
	 */
	public void setTasks(List<BuildTask> tasks) {
		this.tasks = tasks;
	}

}
