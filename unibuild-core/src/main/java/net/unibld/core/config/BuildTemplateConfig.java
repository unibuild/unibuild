package net.unibld.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Configuration of a build template with dynamic {@link TaskConfigurations} node.
 * @author andor
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "template")
public class BuildTemplateConfig {

	@XmlTransient
	private TaskConfigurations tasks;

	@XmlAttribute
	private String name;

	/**
	 * @return Task configuration of the template
	 */
	public TaskConfigurations getTasks() {
		return tasks;
	}

	/**
	 * @param tasks Task configuration of the template
	 */
	public void setTasks(TaskConfigurations tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return Name of the template
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Name of the template
	 */
	public void setName(String name) {
		this.name = name;
	}

		
}
