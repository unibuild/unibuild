package net.unibld.server.service.security;

import java.util.List;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.service.query.IListQueryExecutor;

public interface UserListService extends IListQueryExecutor<UserProfile> {

	UserItem findUserItem(String rowKey);

	List<Authority> getAuthoritiesForUserIds(List<String> userIds);

	

}
