package net.unibld.server.service.security.invitation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.service.query.AbstractListQueryExecutor;
import net.unibld.server.service.query.ListQueryInput;
import net.unibld.server.service.query.ServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("invitationListService")
public class InvitationListServiceImpl extends AbstractListQueryExecutor<Invitation> implements InvitationListService {
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private InvitationService invitationService;
	
	public ServiceResult<Invitation> getList(ListQueryInput input) {
		return new ServiceResult<Invitation>(getResultList(input,Invitation.class), getTotalCount(input,Invitation.class));
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	


	@Override
	protected String getDefaultOrderColumnName() {
		return "createDate";
	}

	@Override
	public Invitation findInvitation(long id) {
		return invitationService.findInvitation(id);
	}

	
}
