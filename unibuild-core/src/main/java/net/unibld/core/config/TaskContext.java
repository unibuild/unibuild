package net.unibld.core.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.unibld.core.ContextAttribute;
import net.unibld.core.build.IBuildContextAttributeContainer;
import net.unibld.core.var.VariableSupport;

/**
 * A class that contains the settings that configure a BuildTask via the
 * TaskConfig configuration class.
 * @author andor
 * @version 1.0
 * @updated 22-05-2013 3:47:34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "context")
public class TaskContext implements Serializable, Cloneable { 
	@XmlTransient
	private Map<String,String> attributesMap;
	
	@XmlTransient
	private Map<String,Serializable> serializableAttributesMap;
	
	@XmlElement(name="attribute")
	private List<ContextAttribute> attributes;
	
	/**
	 * Default constructor
	 */
	public TaskContext(){
		attributesMap=new Hashtable<>();
		serializableAttributesMap=new Hashtable<>();
		attributes=new ArrayList<>();
	}

	public Set<String> getAttributeKeys() {
		if (attributesMap==null||attributesMap.size()==0) {
			if (attributesMap==null) {
				attributesMap=new Hashtable<>();
			}
			for (ContextAttribute ca:attributes) {
				attributesMap.put(ca.getName(), ca.getValue());
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
		ContextAttribute ca = new ContextAttribute();
		ca.setName(name);
		ca.setValue(value);
		attributes.add(ca);
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
		ContextAttribute ca = new ContextAttribute();
		ca.setName(name);
		ca.setSerializable(value);
		
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
			if (attributes!=null) {
				for (ContextAttribute a:attributes) {
					attributesMap.put(a.getName(), a.getValue());
				}
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
		if (this.attributes!=null) {
			c2.attributes=new ArrayList<>();
			c2.attributes.addAll(this.attributes);
		}
		if (this.attributesMap!=null) {
			c2.attributesMap=new HashMap<>();
			c2.attributesMap.putAll(this.attributesMap);
		}
		if (this.serializableAttributesMap!=null) {
			c2.serializableAttributesMap=new HashMap<>();
			c2.serializableAttributesMap.putAll(this.serializableAttributesMap);
		}
		return c2;
	}

	
}//end TaskContext