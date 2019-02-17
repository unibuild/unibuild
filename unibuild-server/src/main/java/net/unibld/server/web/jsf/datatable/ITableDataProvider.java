package net.unibld.server.web.jsf.datatable;

import java.util.Map;

import net.unibld.server.service.query.ServiceResult;

import org.primefaces.model.SortOrder;

/**
 * Generic interface for loading lazy-loading and filterable PrimeFaces datatables automatically.
 * @author andor
 *
 * @param <T> Generic datatable row type
 */
public interface ITableDataProvider<T> {

	/**
	 * Returns a row identified by a unique string row key.
	 * @param rowKey Unique row key
	 * @return The row identified by the key
	 */
	T getRowById(String rowKey);
	/**
	 * Returns the unique id of a row (typically a primary key)
	 * @param item Row
	 * @return Unique id of the row
	 */
	Object getId(T item);
	/**
	 * Returns a result for a PrimeFaces datatable that contains the total row count and a single
	 * page of the matching records
	 * @param first First record to return
	 * @param pageSize Page size
	 * @param filters Filter map
	 * @param sortField Sort field
	 * @param sortOrder Sorting order
	 * @return A result with total row count and a single page
	 */
	ServiceResult<T> getListWithCount(int first, int pageSize,Map<String,Object> filters, String sortField,
			SortOrder sortOrder);
}
