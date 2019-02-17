package net.unibld.server.web.jsf.datatable;

import java.util.Map;

import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;
import net.unibld.server.service.security.invitation.InvitationListService;
import net.unibld.server.web.jsf.view.InvitationTableBean;

import org.primefaces.model.SortOrder;

public class InvitationListProvider extends AbstractTableDataProvider<Invitation> implements ITableDataProvider<Invitation> {
	private InvitationListService invitationListService;
	private InvitationTableBean bean;
	
	public InvitationListProvider(InvitationListService listService,InvitationTableBean bean) {
		this.invitationListService=listService;
		this.bean=bean;
	}
	
	@Override
	public Object getId(Invitation item) {
		return item.getId();
	}

	@Override
	protected Invitation findById(String rowKey) {
		return invitationListService.findInvitation(Long.parseLong(rowKey));

	}


	@Override
	public ServiceResult<Invitation> getListWithCount(int first, int pageSize,Map<String,Object> filters,
			String sortField, SortOrder sortOrder) {
		if (bean.getStatusFilter()!=null) {
			filters.put("status",bean.getStatusFilter());
		}
		
		ListQueryInput input = toInput(first,pageSize,filters,sortField,sortOrder);
		return invitationListService.getList(input);
	}

	
}
