package net.unibld.server.service.security.ticket;

import java.util.List;

import net.unibld.server.entities.security.ticket.TempCredential;
import net.unibld.server.entities.security.ticket.UserTicket;
import net.unibld.server.entities.security.ticket.UserTicket.TicketType;

public interface UserTicketService {

	UserTicket createTicket(String userName, TicketType ticketType);

	UserTicket findUserTicket(String id);

	TempCredential getTempCredential(String userName);

	void deleteTicket(UserTicket t);

	UserTicket getValidTicketById(String id);
	void touchTicket(UserTicket ticket);

	UserTicket getTicketByUser(String userName, TicketType type);
	UserTicket createAccessTicket(String userName);
	
	void deleteAccessTicket(String userName);
	List<UserTicket> getAccessTicketsForUserIds(List<String> userIds);
}
