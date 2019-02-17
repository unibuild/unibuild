package net.unibld.core.build;

import java.util.Map;

public class IncludeContext implements IBuildContextAttributeContainer {
	private Map<String, String> variables;
	
	public IncludeContext(IBuildContextAttributeContainer container) {
		this.variables=container.getTaskContextAttributeMap();
	}

	@Override
	public String getBuildContextAttribute(String name) {
		return variables.get(name);
	}

	@Override
	public Map<String, String> getTaskContextAttributeMap() {
		return variables;
	}

	/**
	 * @param userVars Map of user-defined variables
	 */
	public void setVariables(Map<String, String> userVars) {
		throw new UnsupportedOperationException();
	}

	public void setVariable(String varName, String varValue) {
		variables.put(varName, varValue);
	}

}
