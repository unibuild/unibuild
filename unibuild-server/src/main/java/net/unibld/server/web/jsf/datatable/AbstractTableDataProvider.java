package net.unibld.server.web.jsf.datatable;

import java.util.Hashtable;
import java.util.Map;

import net.unibld.server.service.query.ListQueryInput;

import org.primefaces.model.SortOrder;

/**
 * Abstract class for data providers for lazy-loading PrimeFaces datatables, implementing the
 * {@link ITableDataProvider} interface
 * @author andor
 *
 * @param <T> Generic datatable row type
 */
public abstract class AbstractTableDataProvider<T> implements ITableDataProvider<T>{
	private Map<String,T> mapById=new Hashtable<String, T>();
	
	protected void putToCache(String id,T elem) {
		mapById.put(id, elem);
	}
	
	protected T getFromCache(String id) {
		return mapById.get(id);
	}
	
	@Override
	public T getRowById(String rowKey) {
		if (mapById.containsKey(rowKey)) {
			return getFromCache(rowKey);
		}
		T found=findById(rowKey);
		if (found==null) {
			throw new IllegalArgumentException("Invalid row key: "+rowKey);
		}
		putToCache(rowKey, found);
		return found;
	}

	protected abstract T findById(String rowKey);
	
	protected ListQueryInput toInput(int first, int pageSize, Map<String,Object> filters,String sortField,
			SortOrder sortOrder) {
		ListQueryInput i=new ListQueryInput();
		i.setFirst(first);
		i.setMax(pageSize);
		i.setOrderColumn(sortField);
		i.setOrderAsc(sortOrder!=null?sortOrder==SortOrder.ASCENDING:false);
		i.setFilters(filters);
		return i;
	}
}
