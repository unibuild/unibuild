package net.unibld.server.service.security.activation;

import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.service.security.SpringSecurityException;

public interface ActivationService {
	UserTicket getActivationTicket(String ticket) throws SpringSecurityException;
	UserProfile activateUser(UserProfile profile, String password) throws SpringSecurityException;
	UserProfile activateUser(String activationCode, String password) throws SpringSecurityException;
}
