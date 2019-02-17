package net.unibld.server.service.security.ticket;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;

import net.unibld.server.entities.security.ticket.TempCredential;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.entities.security.ticket.UserTicket.TicketExpiration;
import net.unibld.server.entities.security.ticket.UserTicket.TicketType;
import net.unibld.server.repositories.security.ticket.TempCredentialRepository;
import net.unibld.server.repositories.security.ticket.UserTicketRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userTicketService")
public class UserTicketServiceImpl implements UserTicketService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserTicketServiceImpl.class);
	
	@Value("${ticket.expiration.secs:3600}")
	private int ticketExpirationSecs;
	
	@Autowired
	private UserTicketRepository userTicketRepo;
	@Autowired
	private TempCredentialRepository tempCredentialRepo;
	
	@Transactional
	public UserTicket createAccessTicket(String userName) {
		UserTicket ticket = getTicketByUser(userName, TicketType.REMOTE_ACCESS);
		if (ticket != null) {
			deleteTicket(ticket.getId());
		}

		UserTicket t = new UserTicket();
		String uid = UUID.randomUUID().toString();
		t.setId(uid);
		t.setUserName(userName);
		t.setCreateDate(new Date());
		t.setType(TicketType.REMOTE_ACCESS);
		t.setExpiration(TicketExpiration.PERSISTENT);
		return userTicketRepo.save(t);

	}
	@Transactional
	public UserTicket createTicket(String userName, TicketType type) {
		UserTicket ticket = getTicketByUser(userName, type);
		if (ticket != null) {
			deleteTicket(ticket.getId());
		}

		UserTicket t = new UserTicket();
		String uid = UUID.randomUUID().toString();
		t.setId(uid);
		t.setUserName(userName);
		t.setCreateDate(new Date());
		t.setType(type);
		t.setExpiration(TicketExpiration.TEMPORARY);
		return userTicketRepo.save(t);

	}
	public UserTicket getTicketByUser(String userName, TicketType type) {
		List<UserTicket> result=userTicketRepo.findByUserNameAndTypeOrderByCreateDateDesc(userName,type);
		if (result.size()==0) {
			return null;
		}
		return result.get(0);
		
	
	}

	
	@Transactional
	public void deleteTicket(String id) {
		try {
			UserTicket t =userTicketRepo.findOne(id);
			if (t != null) {
				userTicketRepo.delete(t);
			}
		} catch (NoResultException e) {
			//id not found
			LOGGER.warn("Ticket id to delete not found: " + id);
		}
	}

	@Override
	public UserTicket findUserTicket(String id) {
		return userTicketRepo.findOne(id);
	}
	public void touchTicket(UserTicket ticket) {
		if (ticket==null) {
			LOGGER.warn("Ticket to touch was null");
			return;
		}
		
		if (ticket.getType()!=TicketType.AUTHENTICATION) {
			LOGGER.warn(String.format("Invalid ticket type (%s): %s",ticket.getType().name(),ticket.getId()));
			return;
		}
		
		Date now=new Date();
		if (!ticket.isValid(now.getTime(),ticketExpirationSecs)) {
			LOGGER.warn("Ticket to touch already expired: "+ticket.getId());
			return;
		}
		
		ticket.setCreateDate(new Date());
		userTicketRepo.save(ticket);
		
	}
	
	public UserTicket getValidTicketById(String id) {
		UserTicket ticket = findUserTicket(id);
		
		if (ticket==null) {
			return null;
		}
		
		if (ticket.getType()!=TicketType.AUTHENTICATION&&ticket.getType()!=TicketType.REMOTE_ACCESS) {
			return null;
		}
		
		Date now=new Date();
		if (!ticket.isValid(now.getTime(),ticketExpirationSecs)) {
			return null;
		}
		return ticket;
	}
	
	public TempCredential getTempCredential(String userName) {
		return tempCredentialRepo.findOne(userName);
	}

	@Override
	@Transactional
	public void deleteTicket(UserTicket t) {
		userTicketRepo.delete(t);
	}
	
	@Override
	@Transactional
	public void deleteAccessTicket(String userName) {
		userTicketRepo.deleteByUserNameAndType(userName,TicketType.REMOTE_ACCESS);
	}
	@Override
	public List<UserTicket> getAccessTicketsForUserIds(List<String> userIds) {
		return userTicketRepo.findByUserNameInAndType(userIds,TicketType.REMOTE_ACCESS);
	}	
}
