package net.unibld.server.service.build;

import net.unibld.core.persistence.model.Build;
import net.unibld.core.persistence.model.Project;
import net.unibld.server.entities.build.RemoteBuild;
import net.unibld.server.service.query.IListQueryExecutor;
/**
 * A service interface that exposes functionality related the entity {@link Build}, that represents a single
 * build run in the system
 * @author andor
 */
public interface BuildListService extends IListQueryExecutor<Build>{

	/**
	 * Finds a build record by its unique GUID
	 * @param id Unique build GUID
	 * @return Build record
	 */
	Build findBuild(String id);
	
}
