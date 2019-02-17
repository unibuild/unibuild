package net.unibld.server.web.jsf.datatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;
import net.unibld.server.service.security.UserItem;
import net.unibld.server.service.security.UserListService;
import net.unibld.server.service.security.ticket.UserTicketService;
import net.unibld.server.web.jsf.view.UserTableBean;

import org.primefaces.model.SortOrder;

public class UserListProvider extends AbstractTableDataProvider<UserItem> implements ITableDataProvider<UserItem> {
	private UserListService userListService;
	private UserTableBean bean;
	private UserTicketService userTicketService;
	
	
	public UserListProvider(UserListService listService,UserTicketService ticketService,UserTableBean bean) {
		this.userListService=listService;
		this.userTicketService=ticketService;
		this.bean=bean;
	}
	
	@Override
	public Object getId(UserItem item) {
		return item.getProfile().getUserName();
	}

	@Override
	protected UserItem findById(String rowKey) {
		return userListService.findUserItem(rowKey);

	}


	@Override
	public ServiceResult<UserItem> getListWithCount(int first, int pageSize,Map<String,Object> filters,
			String sortField, SortOrder sortOrder) {
		filters=reindexFilters(filters);
		if (bean.getStatusFilter()!=null) {
			filters.put("status",bean.getStatusFilter());
		}
		
		if (sortField!=null&&sortField.startsWith("profile.")) {
			sortField=sortField.replace("profile.", "");
		}
		ListQueryInput input = toInput(first,pageSize,filters,sortField,sortOrder);
		return toResult(userListService.getList(input));
	}

	private Map<String, Object> reindexFilters(Map<String, Object> filters) {
		Map<String,Object> ret=new HashMap<String, Object>();
		for (String key:filters.keySet()) {
			if (key.startsWith("profile.")) {
				String newKey=key.replace("profile.", "");
				ret.put(newKey, filters.get(key));
			} else {
				ret.put(key, filters.get(key));
			}
		}
		return ret;
	}

	private ServiceResult<UserItem> toResult(ServiceResult<UserProfile> list) {
		ServiceResult<UserItem> ret=new ServiceResult<UserItem>();
		ret.setTotalCount(list.getTotalCount());
		ret.setResultList(toItemList(list.getResultList()));
		return ret;
	}

	private List<UserItem> toItemList(List<UserProfile> resultList) {
		List<String> userIds=new ArrayList<String>();
		Map<String,UserItem> map=new HashMap<String, UserItem>();
		List<UserItem> ret=new ArrayList<UserItem>();
		for (UserProfile p:resultList) {
			userIds.add(p.getUserName());
			UserItem item=new UserItem();
			item.setProfile(p);
			ret.add(item);
			map.put(p.getUserName(), item);
		}
		
		List<Authority> authorities=userListService.getAuthoritiesForUserIds(userIds);
		for (Authority a:authorities) {
			if (map.containsKey(a.getUser().getUsername())) {
				map.get(a.getUser().getUsername()).setAuthority(a);
			} else {
				throw new IllegalStateException("No UserProfile found for Authority: "+a.getUser().getUsername());
			}
		}
		
		List<UserTicket> tickets=userTicketService.getAccessTicketsForUserIds(userIds);
		for (UserTicket a:tickets) {
			if (map.containsKey(a.getUserName())) {
				map.get(a.getUserName()).setAccessTicket(a);
			} 
		}
		return ret; 
	}
}
