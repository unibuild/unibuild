package net.unibld.server.web.jsf.view;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Base class for generic managed beans that support PrimeFaces datatables either in a lazy-loading
 * or in an eager-loading manner.
 * @author andor
 *
 * @param <T>
 */
public abstract class AbstractModelTableBean<T> {
	protected T selectedItem;

	/**
	 * @return Selected item of the datatable
	 */
	public T getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @param selectedItem Selected item of the datatable
	 */
	public void setSelectedItem(T selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	protected String getAuthenticatedUserName() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth==null) {
			return null;
		}
		return auth.getName();
	}
}
