package net.unibld.server.service.security.mail;

import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.invitation.Invitation;
import net.unibld.server.service.security.SpringSecurityException;

/**
 * An interface for user management related mail sending.
 * @author andor
 *
 */
public interface SecurityMailService {

	void sendInvitationMail(Invitation inv) throws SpringSecurityException;

	void sendEmailChangeMail(UserProfile profile, String newEmail);

	void sendForgottenPasswordMail(String email) throws SpringSecurityException;

	void sendPasswordChangeMail(String email) throws SpringSecurityException;

}
