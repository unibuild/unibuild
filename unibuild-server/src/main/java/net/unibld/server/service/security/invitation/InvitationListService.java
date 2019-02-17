package net.unibld.server.service.security.invitation;

import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.service.query.IListQueryExecutor;

public interface InvitationListService extends IListQueryExecutor<Invitation> {

	Invitation findInvitation(long id);

}
