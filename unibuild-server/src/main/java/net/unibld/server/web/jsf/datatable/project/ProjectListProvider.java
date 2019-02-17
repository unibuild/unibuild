package net.unibld.server.web.jsf.datatable.project;

import java.util.Map;

import net.unibld.core.persistence.model.Project;
import net.unibld.server.entities.log.Log;
import net.unibld.server.service.log.LogService;
import net.unibld.server.service.project.ProjectListService;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;
import net.unibld.server.web.jsf.datatable.AbstractTableDataProvider;
import net.unibld.server.web.jsf.datatable.ITableDataProvider;

import org.primefaces.model.SortOrder;

public class ProjectListProvider extends AbstractTableDataProvider<Project> implements ITableDataProvider<Project> {
	private ProjectListService projectListService;
	
	public ProjectListProvider(ProjectListService listService) {
		this.projectListService=listService;
	}
	
	@Override
	public Object getId(Project item) {
		return item.getId();
	}

	@Override
	protected Project findById(String rowKey) {
		return projectListService.findProject(rowKey);

	}


	@Override
	public ServiceResult<Project> getListWithCount(int first, int pageSize,Map<String,Object> filters,
			String sortField, SortOrder sortOrder) {
		ListQueryInput input = toInput(first,pageSize,filters,sortField,sortOrder);
		return projectListService.getList(input);
	}
}
