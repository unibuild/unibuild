package net.unibld.server.web.jsf.datatable.log;

import java.util.Map;

import net.unibld.server.entities.log.Log;
import net.unibld.server.service.log.LogService;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;
import net.unibld.server.web.jsf.datatable.AbstractTableDataProvider;
import net.unibld.server.web.jsf.datatable.ITableDataProvider;

import org.primefaces.model.SortOrder;

public class LogListProvider extends AbstractTableDataProvider<Log> implements ITableDataProvider<Log> {
	private LogService logService;
	
	public LogListProvider(LogService listService) {
		this.logService=listService;
	}
	
	@Override
	public Object getId(Log item) {
		return item.getId();
	}

	@Override
	protected Log findById(String rowKey) {
		return logService.findLog(Long.parseLong(rowKey));

	}


	@Override
	public ServiceResult<Log> getListWithCount(int first, int pageSize,Map<String,Object> filters,
			String sortField, SortOrder sortOrder) {
		ListQueryInput input = toInput(first,pageSize,filters,sortField,sortOrder);
		return logService.getList(input);
	}
}
