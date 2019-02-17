package net.unibld.server.service.security.passwordchange;

import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.service.security.SpringSecurityException;

public interface PasswordChangeService {

	UserTicket getChangePasswordTicket(String ticket) throws SpringSecurityException;

	String changePassword(String ticket, String password) throws SpringSecurityException;

}
