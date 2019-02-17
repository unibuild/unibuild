package net.unibld.server.web.jsf.datatable.build;

import java.util.Map;

import org.primefaces.model.SortOrder;

import net.unibld.core.persistence.model.Build;
import net.unibld.server.service.build.BuildListService;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;
import net.unibld.server.web.jsf.datatable.AbstractTableDataProvider;
import net.unibld.server.web.jsf.datatable.ITableDataProvider;

public class BuildListProvider extends AbstractTableDataProvider<Build> implements ITableDataProvider<Build> {
	private BuildListService buildListService;
	
	public BuildListProvider(BuildListService listService) {
		this.buildListService=listService;
	}
	
	@Override
	public Object getId(Build item) {
		return item.getId();
	}

	@Override
	protected Build findById(String rowKey) {
		return buildListService.findBuild(rowKey);

	}


	@Override
	public ServiceResult<Build> getListWithCount(int first, int pageSize,Map<String,Object> filters,
			String sortField, SortOrder sortOrder) {
		ListQueryInput input = toInput(first,pageSize,filters,sortField,sortOrder);
		return buildListService.getList(input);
	}
}
