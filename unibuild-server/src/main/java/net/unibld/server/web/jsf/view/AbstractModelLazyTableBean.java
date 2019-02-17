package net.unibld.server.web.jsf.view;

import net.unibld.server.web.jsf.datatable.ITableDataProvider;
import net.unibld.server.web.jsf.datatable.LazyTableDataModel;

import org.primefaces.model.LazyDataModel;

/**
 * Base class for a JSF managed bean that loads a generic datatable in a lazy manner.
 * @author andor
 *
 * @param <T> Generic parameter that is the type of the datatable row.
 */
public abstract class AbstractModelLazyTableBean<T> extends AbstractModelTableBean<T> {
	private LazyDataModel<T> lazyModel;

	/**
	 * Returns the underlying PrimeFaces lazy data model
	 * @return PrimeFaces lazy data model
	 */
	public LazyDataModel<T> getLazyModel() {
		if (lazyModel==null) {
			lazyModel=new LazyTableDataModel<>(createDataProvider());
		}
		return lazyModel;
	}  
	
	
	protected void refresh() {
		lazyModel=null;
		
	}
	protected abstract ITableDataProvider<T> createDataProvider();
}
