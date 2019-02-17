package net.unibld.server.service.security.activation;

import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.UserStatus;
import net.unibld.server.entities.security.ticket.TempCredential;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.entities.security.ticket.UserTicket.TicketType;
import net.unibld.server.repositories.security.UserProfileRepository;
import net.unibld.server.service.security.SpringSecurityException;
import net.unibld.server.service.security.ticket.UserTicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivationServiceImpl implements ActivationService {
	@Value("${activation.valid.period.days:1}")
	private int activationValidPeriodDays;
	
	@Autowired
	private UserTicketService ticketService;
	@Autowired
	private UserDetailsManager userDetailsManager;
	@Autowired
	private UserProfileRepository userProfileRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	/**
	 * @param ticket Ticket ID
	 * @return {@link UserTicket} for user activation
	 * @throws SpringSecurityException
	 */
	public UserTicket getActivationTicket(String ticket) throws SpringSecurityException {
		UserTicket t = ticketService.findUserTicket(ticket);
		
		if (t==null) {
			throw new SpringSecurityException("Ticket was null");
		}
		
		if (t.getType()!=TicketType.ACTIVATION) {
			throw new SpringSecurityException("Ticket type was not TicketType.ACTIVATION");
		}
		if (!t.isValid(System.currentTimeMillis(), activationValidPeriodDays*24*60*60)) {
			throw new SpringSecurityException("Ticket is not valid: created "+t.getCreateDate().toString());
		}
		return t;
	}
	
	@Transactional
	public UserProfile activateUser(UserProfile profile, String password) throws SpringSecurityException {
		if (profile==null) {
			throw new SpringSecurityException("User profile was null");
		}
		
		if (profile.isActivated()) {
			throw new SpringSecurityException("User profile already activated: "+profile.getUserName());
		}

		
		if (password!=null) {
			
			String encPasswd = passwordEncoder.encode(password);
			TempCredential tmp = ticketService.getTempCredential(profile.getUserName());
			if (tmp==null) {
				throw new SpringSecurityException("Temp credential not found: "+profile.getUserName());
			}
			userDetailsManager.changePassword(tmp.getPassword(), encPasswd);
		}
		
		profile.setActivated(true);
		profile.setStatus(UserStatus.REGISTERED);
		return userProfileRepo.save(profile);
	}
	
	@Transactional
	public UserProfile activateUser(String ticket,String password) throws SpringSecurityException {
		UserTicket t = getActivationTicket(ticket);
		if (t==null) {
			throw new IllegalArgumentException("Invalid ticket id: "+ticket);
		}
		UserProfile profile = userProfileRepo.findOne(t.getUserName());
		if (profile==null) {
			throw new IllegalArgumentException("Invalid ticket user id: "+t.getUserName());
		}
		UserProfile ret = activateUser(profile, password);
		
		ticketService.deleteTicket(t);
		return ret;
	}
}
