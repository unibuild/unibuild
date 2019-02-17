package net.unibld.server.service.project;

import net.unibld.core.persistence.model.Project;
import net.unibld.server.service.query.IListQueryExecutor;

public interface ProjectListService extends IListQueryExecutor<Project>{

	Project findProject(String id);

	
}
