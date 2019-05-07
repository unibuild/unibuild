package net.unibld.core.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import net.unibld.core.BuildTask;
import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.var.VariableSupport;

/**
 * Context for a {@link BuildTask}
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:34
 */
public class TaskContext implements Serializable, Cloneable { 
	private String buildId;
	private int taskIndex;
	
	private String failureReason;
	private ProjectConfig projectConfig;
	private BuildGoalConfig goalConfig;
	
	private Map<String,String> attributesMap;
	private Map<String,Serializable> serializableAttributesMap;
	
	
	/**
	 * Default constructor
	 */
	public TaskContext(){
		attributesMap=new Hashtable<>();
		serializableAttributesMap=new Hashtable<>();
		
	}

	public Set<String> getAttributeKeys() {
		if (attributesMap==null||attributesMap.size()==0) {
			if (attributesMap==null) {
				attributesMap=new Hashtable<>();
			}
			
		}
		return attributesMap.keySet();
	}
	
	/**
	 * Adds a string attribute to the context
	 * @param name Name of the attribute
	 * @param value Attribute value
	 */
	public void addAttribute(String name,String value) {
		attributesMap.put(name, value);
	}
	
	/**
	 * Adds a serializable attribute to the context
	 * @param name Name of the attribute
	 * @param value Attribute value object
	 */
	public void addSerializableAttribute(String name,Serializable value) {
		if (serializableAttributesMap==null) {
			serializableAttributesMap=new Hashtable<>();
		}
		serializableAttributesMap.put(name, value);
	}
	
	
	/**
	 * Returns the value of a string attribute with variables substituted using {@link VariableSupport}
	 * @param attrContainer Parent attribute container
	 * @param name Attribute name
	 * @return Substituted string value
	 */
	public String getAttribute(VariableSupport vars,IBuildContextAttributeContainer container, String name) {
		if (attributesMap==null||attributesMap.size()==0) {
			if (attributesMap==null) {
				attributesMap=new Hashtable<>();
			}
		}
		
		return vars.substitute(attributesMap.get(name),container);
	}
	
	public Serializable getSerializable(String name) {
		if (serializableAttributesMap==null) {
			serializableAttributesMap=new Hashtable<>();
		}
		return serializableAttributesMap.get(name);
	}

	

	public Map<String, String> getAttributeMap() {
		return attributesMap;
	}

	public void setAttributeMap(Map<String, String> attributeMap) {
		attributesMap=attributeMap;
	}

	public TaskContext getClone()  {
		TaskContext c2=new TaskContext();
		if (this.attributesMap!=null) {
			c2.attributesMap=new HashMap<>();
			c2.attributesMap.putAll(this.attributesMap);
		}
		if (this.serializableAttributesMap!=null) {
			c2.serializableAttributesMap=new HashMap<>();
			c2.serializableAttributesMap.putAll(this.serializableAttributesMap);
		}
		c2.buildId=this.buildId;
		return c2;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public ProjectConfig getProjectConfig() {
		return projectConfig;
	}

	public void setProjectConfig(ProjectConfig projectConfig) {
		this.projectConfig = projectConfig;
	}

	public BuildGoalConfig getGoalConfig() {
		return goalConfig;
	}

	public void setGoalConfig(BuildGoalConfig goalConfig) {
		this.goalConfig = goalConfig;
	}

	public int getTaskIndex() {
		return taskIndex;
	}

	public void setTaskIndex(int taskIndex) {
		this.taskIndex = taskIndex;
	}

	
	
}//end TaskContext