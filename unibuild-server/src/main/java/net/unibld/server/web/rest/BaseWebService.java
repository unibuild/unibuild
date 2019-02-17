package net.unibld.server.web.rest;

import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.entities.security.ticket.UserTicket.TicketExpiration;
import net.unibld.server.service.security.ticket.UserTicketService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * Abstract MVC-based web service class (for mobile clients and .NET client)
 * @author andor
 *
 */
public abstract class BaseWebService {
	
	@Autowired
	protected UserTicketService ticketService;
	
	protected String doAuth(String ticket) {
		
		if (ticket!=null&&ticket.trim().length()>0) {
			UserTicket t=ticketService.getValidTicketById(ticket);
			if (t==null) {
				
				LoggerFactory.getLogger(getClass()).warn("Request ticket not found or not valid: "+ticket);
				throw new RestSecurityException("Request not authenticated");
			} else {
				if (t.getExpiration()==TicketExpiration.TEMPORARY) {
					ticketService.touchTicket(t);
				}
			}
			return t.getUserName();
		}
		
		
		LoggerFactory.getLogger(getClass()).warn("Requested ticket was null");
		throw new RestSecurityException("Ticket was null");
	}
		
}
