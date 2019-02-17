package net.unibld.server.service.security.passwordchange;

import net.unibld.server.entities.security.User;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.entities.security.ticket.UserTicket.TicketType;
import net.unibld.server.repositories.security.UserRepository;
import net.unibld.server.service.security.SpringSecurityException;
import net.unibld.server.service.security.ticket.UserTicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("passwordChangeService")
public class PasswordChangeServiceImpl implements PasswordChangeService {
	@Autowired
	private UserTicketService ticketService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	/**
	 * @param ticket Ticket ID
	 * @return {@link UserTicket} for password change
	 * @throws EwiseSecurityException
	 */
	public UserTicket getChangePasswordTicket(String ticket) throws SpringSecurityException {
		UserTicket t = ticketService.findUserTicket(ticket);
		
		if (t==null) {
			throw new SpringSecurityException("Ticket was null");
		}
		
		if (t.getType()!=TicketType.PASSWORD_CHANGE) {
			throw new SpringSecurityException("Ticket type was not TicketType.PASSWORD_CHANGE");
		}
		//1 hour
		if (!t.isValid(System.currentTimeMillis(), 60*60)) { 
			throw new SpringSecurityException("Ticket is not valid: created "+t.getCreateDate().toString());
		}
		return t;
	}
	
	/**
	 * Changes the password of a user using the specified password change ticket
	 * @param ticket Password change ticket
	 * @param password New password
	 * @throws EwiseSecurityException
	 */
	@Transactional
	public String changePassword(String ticket, String password) throws SpringSecurityException {
		UserTicket t = getChangePasswordTicket(ticket);
		if (t==null) {
			throw new IllegalArgumentException("Invalid ticket: "+ticket);
		}
		

		String encPasswd = passwordEncoder.encode(password);
	
		User user = userRepository.findOne(t.getUserName());
		if (user==null) {
			throw new IllegalArgumentException("Invalid user name: "+t.getUserName());
		}
		user.setPassword(encPasswd);
		userRepository.save(user);
		ticketService.deleteTicket(t);
		return t.getUserName();
		
	}
}
