package net.unibld.server.service.security.emailchange;

import java.util.Date;

import net.unibld.server.entities.security.EmailChangeRequest;
import net.unibld.server.entities.security.UserProfile;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.entities.security.ticket.UserTicket.TicketType;
import net.unibld.server.repositories.security.EmailChangeRequestRepository;
import net.unibld.server.repositories.security.UserProfileRepository;
import net.unibld.server.repositories.security.ticket.UserTicketRepository;
import net.unibld.server.service.security.SecurityService;
import net.unibld.server.service.security.SpringSecurityException;
import net.unibld.server.service.security.ticket.UserTicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailChangeServiceImpl implements EmailChangeService{
	@Autowired
	private SecurityService securityService;
	@Autowired
	private UserTicketService userTicketService;
	@Autowired
	private EmailChangeRequestRepository emailChangeRepo;
	@Autowired
	private UserProfileRepository userProfileRepo;
	@Autowired
	private UserTicketRepository userTicketRepo;
	
	public EmailChangeRequest getEmailChangeRequest(String authCode) throws SpringSecurityException {
		UserTicket t = userTicketService.findUserTicket(authCode);

		if (t == null) {
			throw new SpringSecurityException("Ticket was null");
		}

		if (t.getType() != TicketType.EMAIL_CHANGE) {
			throw new SpringSecurityException(
					"Ticket type was not TicketType.EMAIL_CHANGE");
		}
		// 1 hour
		Date now=new Date();
		if (!t.isValid(now.getTime(), 60 * 60)) {
			throw new SpringSecurityException("Ticket is not valid: created "
					+ t.getCreateDate().toString());
		}
		return getEmailChangeRequestForTicket(t);
	}
	private EmailChangeRequest getEmailChangeRequestForTicket(UserTicket t) throws SpringSecurityException {
		EmailChangeRequest ret=emailChangeRepo.findByTicket(t.getId());
		if (ret==null) {
			throw new SpringSecurityException("No EmailChangeRequest found for ticket: "+t.getId());
		}
		return ret;
	}
	
	@Transactional
	public void createEmailChangeRequest(UserTicket t,String email) {
		EmailChangeRequest r=new EmailChangeRequest();
		r.setEmail(email);
		r.setTicket(t.getId());
		r.setUserId(t.getUserName());
		
		emailChangeRepo.save(r);
	}

	@Transactional
	public String changeEmail(EmailChangeRequest r) {
		UserProfile profile = securityService.findUserProfile(r.getUserId());
		if (profile==null ) {
			throw new IllegalArgumentException("UserProfile doesn't exist: "+r.getUserId());
		}
		

		UserTicket t = userTicketRepo.findOne(r.getTicket());
		if (t==null) {
			throw new IllegalArgumentException("Invalid EmailChangeRequest ticket id");
		}
		
		profile.setEmail(r.getEmail());
		userProfileRepo.save(profile);
		
		userTicketRepo.delete(t);
		emailChangeRepo.delete(r);
		return r.getEmail();
	}
	
	
}
