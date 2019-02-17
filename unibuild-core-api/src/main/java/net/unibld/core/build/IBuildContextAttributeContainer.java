package net.unibld.core.build;

import java.util.Map;

/**
 * An interface that defines an access method to named build context attributes.
 * @author andor
 *
 */
public interface IBuildContextAttributeContainer {
	/**
	 * Returns the value of a context attribute
	 * @param name Attribute name
	 * @return Attribute value or null
	 */
	public String getBuildContextAttribute(String name);

	/**
	 * @return Map of task context attributes
	 */
	public Map<String,String> getTaskContextAttributeMap();
	
	/**
	 * @param userVars Map of user-defined variables
	 */
	public void setVariables(Map<String, String> userVars);
}
