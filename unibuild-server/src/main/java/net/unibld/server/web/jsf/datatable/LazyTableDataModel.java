package net.unibld.server.web.jsf.datatable;
  
import java.util.List;
import java.util.Map;

import net.unibld.server.service.query.ServiceResult;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
  
/** 
 * An implementation of PrimeFaces {@link LazyDataModel} that uses an {@link ITableDataProvider} 
 * implementation to provide data for a lazy-loading datatable. 
 * @param <T> Generic table row parameter
 */  
public class LazyTableDataModel<T> extends LazyDataModel<T> {  
      
    
	private static final long serialVersionUID = 474037247866964039L;
	private ITableDataProvider<T> dataProvider;  
      
    /**
     * Constructor with a data provider
     * @param datasource {@link ITableDataProvider} implementation
     */
    public LazyTableDataModel(ITableDataProvider<T> datasource) {  
        this.dataProvider = datasource;  
    }  
      
    @Override  
    public T getRowData(String rowKey) {  
        return dataProvider.getRowById(rowKey);  
    }  
  
    @Override  
    public Object getRowKey(T item) {  
        return dataProvider.getId(item);  
    }  
  
    @Override  
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
    	ServiceResult<T> res = dataProvider.getListWithCount(first, pageSize,filters, sortField, sortOrder);
    	setRowCount(res.getTotalCount());
        return res.getResultList();
    }  
}  