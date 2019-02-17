package net.unibld.core.build;

import java.util.Hashtable;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * A Spring component that holds currently build tool contexts for currently running tasks,
 * hashed by their unique run id.<br>
 * This enables multiple builds to be run at the same time.
 * @author andor
 *
 */
@Component
public class BuildToolContextHolder {
	private Map<String,BuildToolContext> contexts=new Hashtable<>();
	/**
	 * Creates a new context with the specified run id and stores it
	 * @param runId Run id
	 * @return Context object created
	 */
	public BuildToolContext createContext(String runId) {
		BuildToolContext ret = new BuildToolContext(runId);
		contexts.put(runId, ret);
		return ret;
	}

	/**
	 * @param id Run id
	 * @return The context that belongs to the specified run id if it exists or null
	 */
	public BuildToolContext getContext(String id) {
		return contexts.get(id);
	}

	/**
	 * Removes the context from the underlying map, identified by its run id. 
	 * @param id Run id
	 */
	public void removeContext(String id) {
		contexts.remove(id);
	}
	
	/**
	 * @param id Run id
	 * @return True if the run id exists
	 */
	public boolean containsContextId(String id) {
		return contexts.containsKey(id);
	}
}
