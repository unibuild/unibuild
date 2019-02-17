package net.unibld.server.service.security;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import net.unibld.server.entities.security.Authority;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.ticket.UserTicket.TicketType;
import net.unibld.server.service.query.AbstractListQueryExecutor;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;
import net.unibld.server.service.security.ticket.UserTicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userListService")
public class UserListServiceImpl extends AbstractListQueryExecutor<UserProfile> implements UserListService {
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private SecurityService securityService;
	@Autowired
	private UserTicketService ticketService;
	
	public ServiceResult<UserProfile> getList(ListQueryInput input) {
		return new ServiceResult<UserProfile>(getResultList(input,UserProfile.class), getTotalCount(input,UserProfile.class));
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public UserItem findUserItem(String rowKey) {
		UserProfile p = securityService.findUserProfile(rowKey);
		if (p==null) {
			return null;
		}
		
		UserItem item=new UserItem();
		item.setProfile(p);
		List<Authority> authorities = securityService.getAuthorities(p.getUserName());
		if (authorities!=null&&authorities.size()>0) {
			item.setAuthority(authorities.get(0));
		}
		
		item.setAccessTicket(ticketService.getTicketByUser(p.getUserName(),TicketType.REMOTE_ACCESS));
		return item;
	}

	@Override
	public List<Authority> getAuthoritiesForUserIds(List<String> userIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Authority> cq = cb.createQuery(Authority.class);
		Root<Authority> root = cq.from(Authority.class);
		cq.select(root);
		cq=cq.where(root.get("user").get("username").in(userIds));
		
		TypedQuery<Authority> q = em.createQuery(cq);
		return q.getResultList();
		
		
	}

	@Override
	protected String getDefaultOrderColumnName() {
		return "userName";
	}

	
}
