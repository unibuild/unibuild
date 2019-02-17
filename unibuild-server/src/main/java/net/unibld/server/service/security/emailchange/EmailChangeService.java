package net.unibld.server.service.security.emailchange;

import net.unibld.server.entities.security.EmailChangeRequest;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.service.security.SpringSecurityException;

public interface EmailChangeService {

	void createEmailChangeRequest(UserTicket t, String newEmail);

	EmailChangeRequest getEmailChangeRequest(String authCode) throws SpringSecurityException;

	String changeEmail(EmailChangeRequest r);

}
