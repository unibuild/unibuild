package net.unibld.server.service.security.invitation;

import java.util.List;

import net.unibld.server.entities.security.Group;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.service.security.SpringSecurityException;

public interface InvitationService {
	Invitation inviteUser(String invitedEmail, UserProfile invitor,Group group) throws SpringSecurityException;
	UserProfile registerInvitedUser(UserProfile profile,Invitation inv,String password) throws SpringSecurityException;
	Invitation getInvitationSentByAuthCode(String authCode);
	List<Invitation> getInvitationsActive();
	Invitation updateInvitationQueued(Invitation inv);
	Invitation updateInvitationSent(Invitation inv);
	Invitation findInvitation(long id);
	String getRegisterDefaultLanguage();
	Invitation cancelInvitation(long id, String userName);
	Invitation resendInvitation(long id, String userName) throws SpringSecurityException;
	Invitation updateInvitationError(Invitation inv);
}
